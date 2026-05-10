package com.cpmss.workforce.shiftattendancetype;

import com.cpmss.finance.money.Money;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendance;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendanceRepository;
import com.cpmss.hr.lawofshiftattendance.dto.CreateShiftAttendanceLawRequest;
import com.cpmss.hr.lawofshiftattendance.dto.ShiftAttendanceLawResponse;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.common.ShiftTimeWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies effective-dated attendance-law rows attached to shift types.
 */
@ExtendWith(MockitoExtension.class)
class ShiftAttendanceLawWorkflowTest {

    @Mock
    private ShiftAttendanceTypeRepository repository;

    @Mock
    private LawOfShiftAttendanceRepository lawRepository;

    @Mock
    private ShiftAttendanceTypeMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void addsAttendanceLawToShiftType() {
        UUID shiftId = UUID.randomUUID();
        ShiftAttendanceType shift = shift(shiftId);
        LocalDate effectiveDate = LocalDate.of(2026, 5, 1);
        ShiftTimeWindow window = new ShiftTimeWindow(
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        Money overtime = new Money(new BigDecimal("10.00"), "EGP");
        Money discount = new Money(new BigDecimal("5.00"), "EGP");
        when(currentUserService.currentUser()).thenReturn(hrOfficer());
        when(repository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(lawRepository.existsByShiftIdAndEffectiveDate(shiftId, effectiveDate))
                .thenReturn(false);
        when(lawRepository.save(any(LawOfShiftAttendance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShiftAttendanceLawResponse response = service().addAttendanceLaw(shiftId,
                new CreateShiftAttendanceLawRequest(effectiveDate, window,
                        new BigDecimal("8.00"), overtime, discount, "Morning"));

        ArgumentCaptor<LawOfShiftAttendance> lawCaptor =
                ArgumentCaptor.forClass(LawOfShiftAttendance.class);
        verify(lawRepository).save(lawCaptor.capture());
        assertThat(lawCaptor.getValue().getShift()).isSameAs(shift);
        assertThat(response.shiftId()).isEqualTo(shiftId);
        assertThat(response.effectiveDate()).isEqualTo(effectiveDate);
        assertThat(response.expectedHours()).isEqualByComparingTo("8.00");
        assertThat(response.oneHourExtraBonus()).isEqualTo(overtime);
    }

    @Test
    void rejectsDuplicateAttendanceLawDate() {
        UUID shiftId = UUID.randomUUID();
        LocalDate effectiveDate = LocalDate.of(2026, 5, 1);
        when(currentUserService.currentUser()).thenReturn(hrOfficer());
        when(repository.findById(shiftId)).thenReturn(Optional.of(shift(shiftId)));
        when(lawRepository.existsByShiftIdAndEffectiveDate(shiftId, effectiveDate))
                .thenReturn(true);

        assertThatThrownBy(() -> service().addAttendanceLaw(shiftId,
                new CreateShiftAttendanceLawRequest(effectiveDate,
                        new ShiftTimeWindow(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                        new BigDecimal("8.00"), null, null, null)))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(HrErrorCode.SHIFT_ATTENDANCE_LAW_DUPLICATE));
        verify(lawRepository, never()).save(any());
    }

    private ShiftAttendanceTypeService service() {
        return new ShiftAttendanceTypeService(repository, lawRepository, mapper,
                currentUserService);
    }

    private static CurrentUser hrOfficer() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.HR_OFFICER, "hr@example.com");
    }

    private static ShiftAttendanceType shift(UUID id) {
        ShiftAttendanceType shift = ShiftAttendanceType.builder().shiftName("Morning").build();
        shift.setId(id);
        return shift;
    }
}
