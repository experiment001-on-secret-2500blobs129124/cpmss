# Git Commit Convention

This document defines the commit message style for this project, based on
[Conventional Commits](https://www.conventionalcommits.org/).

---

## Format

```
<type>(<scope>): <Subject>

<body>

<footer>
```

---

## Subject Line

```
type(scope): Subject with capital first letter
```

| Rule | Example |
|------|---------|
| Capitalize first word | `feat(auth): Add login validation` ✅ |
| Imperative mood | `Fix bug` ✅ not `Fixed bug` ❌ |
| No period at end | `Add feature` ✅ not `Add feature.` ❌ |
| Max 50 chars (soft limit) | Keep it concise |
| Max 72 chars (hard limit) | GitHub truncates beyond this |

---

## Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `docs` | Documentation only |
| `test` | Adding or updating tests |
| `chore` | Maintenance (deps, config, build) |
| `style` | Formatting, whitespace (no code change) |
| `perf` | Performance improvement |
| `solve` | Complete an exercise, assignment, or project task |
| `learn` | Add or update learning material (captures, notes, curricula) |
| `drill` | Practice exercises, katas, or repetition-based work |
| `audit` | Review, verify, or quality-check existing content |
| `build` | Build system or tooling changes (scripts, runners, Makefiles) |
| `init` | Initialize a new project, module, or repository |
| `deploy` | Deployment-related changes |
| `revert` | Revert a previous commit |
| `ci` | CI/CD pipeline changes |

> The very first commit of any repository is always:
> ```
> init(repo): Ready, Set... Go!
> ```

---

## Scope (Preferred)

The scope provides context about what part of the codebase is affected.

**Why use scope?**
- Instantly know which area changed without reading the body
- Makes `git log --oneline` scannable
- Helps when filtering commits (e.g., `git log --grep="(auth)"`)

**Examples:**
- `feat(auth):` - Authentication module
- `fix(api):` - API layer
- `refactor(users):` - Users module
- `docs(readme):` - README file

Use lowercase. Keep it short (one word preferred).

---

## Body

- Separate from subject with a blank line
- Wrap lines at **72 characters**
- Explain **what** changed and **why** (not how — the code shows how)
- Use bullet points with `-` for lists
- **Start with a 1–2 line summary** before any headers or bullets
- **End with an outcome line** — a closing sentence that states the
  end result or benefit of the change (what the user/system gains)
- Use `##` headers to organize when the commit touches multiple
  concerns (see [Body Sections](#body-sections-for-large-changes))
- **No transient information** — describe the final staged state, not
  the iterative process that got there. Don't reference intermediate
  decisions, discarded approaches, or drafts that never made it into
  the repo history (exception: [The Debrief](#optional-the-debrief-war-story--postmortem-in-code),
  where the user explicitly shares the journey)

### Small Change (No Headers Needed)

When the commit is focused on a single concern, plain bullets or a
short paragraph after the summary line is enough:

```
feat(search): Implement case-insensitive search

Add a dedicated search column so users can query without worrying
about capitalization.

- Add indexed column for efficient case-insensitive queries
- Update search data layer to use dedicated search fields
- Preserve original capitalization for display

Users can now search for "my term" and find "My Term" entries
while the original capitalization is maintained in the UI.
```

---

## Body Sections (For Large Changes)

When a commit spans multiple concerns, use `##` markdown headers to
organize the body. **Every header must have bullet points under it.**

There are two kinds of headers — **thematic** (what the change
achieves) and **component** (where in the codebase it happened).
Mix and match freely; most large commits benefit from both.

### Available Headers

Pick the headers that fit your change:

| Header type | Header | Use when… |
|-------------|--------|-----------|
| Thematic | `## Key Features` | Listing user-facing capabilities added |
| Thematic | `## Architecture` | Explaining structural/design decisions |
| Thematic | `## Benefits` | Clarifying why this approach was chosen |
| Thematic | `## Deployment` / `## DevOps` | Infrastructure or pipeline changes |
| Thematic | `## Backward Compatibility` | Confirming what still works unchanged |
| Thematic | `## Migration Notes` | Steps others must take after pulling |
| Thematic | `## Trade-offs` | Acknowledging known limitations |
| Component | `## Service Layer` | Changes to business logic layer |
| Component | `## Repository Layer` | Changes to data access layer |
| Component | `## API Layer` | Changes to controllers or routes |
| Component | `## Config` / `## Build` | Build system or configuration changes |

These are suggestions — invent headers that fit the change. The goal
is **scannable structure**, not rigid categories.

### Example 1: New Feature

```
feat(config): Implement modular configuration system

Replace monolithic config file with module-based architecture,
enabling users to compose setups from independent modules that
can be mixed and matched.

## Key Features
- Module discovery via marker annotations in config files
- Interactive picker with checkbox selection
- CLI mode for non-interactive installs (--module-a --module-b)
- Smart config merging with conflict detection

## Config Layer
- Declarative module format with validation
- Conflict detection prevents ownership collisions
- Config generation validates all entries before writing

## Deployment Improvements
- Auto-detect artifact filenames from release API
- Fast-path for config-only updates (no binary download)
- Direct copy first, only elevates if permissions fail

## Backward Compatibility
All existing CLI commands unchanged. File handling and
deployment logic preserved.
```

### Example 2: Refactor

```
refactor(users): Modernize user module architecture

Split monolithic service into focused components with proper
type safety and consistent patterns.

## Architecture
- Separate business rules from data access
- Introduce typed DTOs for all request/response contracts

## Service Layer
- Extract validation into dedicated rules class
- Implement factory pattern for entity creation

## Repository Layer
- Replace raw queries with specification pattern
- Add custom query methods for search and filtering

## Backward Compatibility
- All existing API routes work unchanged
- No breaking changes to external interfaces
```

### Example 3: New Subsystem

```
feat(notifications): Add real-time notification system

Introduce a push-based notification pipeline so users receive
updates without polling.

## Key Features
- Subscribe to topics with granular filters
- Batch delivery for high-volume events
- Automatic retry with exponential backoff

## Service Layer
- Event dispatcher routes messages to correct handlers
- Deduplication prevents repeated deliveries

## API Layer
- WebSocket endpoint for live connections
- REST fallback for clients without WebSocket support

## Backward Compatibility
Existing REST polling endpoints remain functional.
Clients can migrate to WebSocket at their own pace.
```

### Optional: The Debrief (War Story / Postmortem in Code)

For commits where the fix was deceptively simple but the journey was
not — hours of debugging, misleading errors, or a one-line fix that
took forever to find. This style is **opt-in** (only use it when the
pain deserves to be documented).

The structure wraps the standard mixed headers inside `## The Fix`:

```
fix(network): Allow outbound DNS on port 53

After 6 hours of debugging silent packet drops, the fix was a
single firewall rule. Documenting this so no one repeats the
investigation.

## The Pain
- Containers could not resolve any external hostnames
- Logs showed no errors — requests just timed out silently
- Tested DNS config, resolver settings, and bridge networking
- Packet capture finally revealed outbound UDP/53 was blocked

## The Fix

### Network Layer
- Add ALLOW rule for outbound UDP/53 in firewall config

### Config
- Add explicit DNS egress entry to environment defaults

### Key Change
- One line. Six hours.

## Lesson Learned
Silent drops with no logging are the default for most firewalls.
Always check packet-level captures before chasing application
config.
```

> **When to use this:** Only when the commit tells a story worth
> preserving — a trap someone else could fall into, a non-obvious
> root cause, or a fix whose simplicity belies the effort.
> The committer decides when it applies.
>
> **AI note:** Never fabricate the Pain section. If the user opts
> into a postmortem, ask them to describe what happened and how it
> felt — then use their own words. The pain must be authentic.

### When to Use Headers

| Commit size | Structure |
|-------------|-----------|
| ≤ 5 bullet points, single concern | Plain bullets, no headers |
| 6+ bullets OR multiple concerns | `##` headers to organize |
| Touches multiple layers for one feature | Mix thematic + component |
| Painful debug or deceptively simple fix | Debrief (opt-in) |

---

## Footer

Reference issues or breaking changes. **Required** when your commit fixes, closes, or introduces a breaking change. Omit if none apply.

```
feat(api): Add user deletion endpoint

BREAKING CHANGE: Removed deprecated /users/remove endpoint

Fixes: #123
Closes: #456
```

---

## Quick Reference

```
50 chars max for subject ----------------------->|
72 chars max for body ------------------------------------------------->|
```

### Good Examples

```
feat(auth): Add refresh token support

- Implement token refresh endpoint
- Add refresh token to login response
- Store refresh tokens with expiration

Users can now stay logged in without re-authenticating.
```

```
fix(leaderboard): Exclude admin users from results

Filter the leaderboard query to remove admin accounts, ensuring
rankings reflect regular users only.

Fixing this prevents inflated scores from skewing the leaderboard.

Fixes: #89
```

```
chore(deps): Bump web framework to 3.0.0
```

### Bad Examples

```
fixed the bug          # No type, not capitalized, vague
```

```
feat: added new feature.   # Past tense, period, vague
```

```
FEAT(AUTH): ADD LOGIN   # All caps
```

---

## Best Practices

### One Commit = One Concern

Each commit should do **one thing**. If you changed two unrelated things, make two commits.

| Situation | Split or Combine? |
|-----------|-------------------|
| Fixed a bug AND added a feature | **Split** — `fix` + `feat` |
| Changed config AND updated docs | **Split** — different scopes |
| Renamed a variable across 5 files | **Combine** — one logical refactor |
| Added a test AND the code it tests | **Combine** — they belong together |
| Fixed typo while working on a feature | **Split** — `style` + `feat` |

### The Atomic Test

> "Could I revert this commit without breaking something unrelated?"

- If **YES** → good commit boundary
- If **NO** → you probably need to combine or reorganize

### Staging Specific Files

```sh
git add file1.py          # Stage one file
git commit -m "fix(auth): Handle expired tokens"

git add file2.py          # Stage another file separately
git commit -m "docs(readme): Update setup instructions"
```

Never `git add .` unless every changed file belongs to the same logical commit.

---

## Tooling

Consider using these tools to enforce the convention:

- [commitlint](https://commitlint.js.org/) - Lint commit messages
- [commitizen](https://commitizen-tools.github.io/commitizen/) - Interactive commit CLI
- [husky](https://typicode.github.io/husky/) - Git hooks for validation
