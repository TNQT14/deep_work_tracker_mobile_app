Design a mobile app called "Deep Work Tracker" — a focus analytics app for knowledge workers (developers, researchers, students). The design should feel premium, data-dense but calm, inspired by Linear + Raycast aesthetic: dark-first, sharp edges, monospace accents, subtle gradients.

---

## Design Language

- Style: Dark-first minimal, slight glassmorphism on cards, monospace numerals for metrics
- Primary color: #6366F1 (Indigo) with glow effect on active states
- Accent: #10B981 (Emerald) for success/completed states
- Background: #0D0F14 (near-black), Surface: #151821
- Typography: System font for body, monospace for timers/numbers
- Corner radius: 12px cards, 8px inputs, 24px buttons
- Motion: Subtle spring animations on state transitions

---

## SCREEN 1 — Session (Home / Start Screen)

**Route**: Bottom tab 1, clock icon

### Idle State (no active session)

- Top: Greeting line "Good morning, Thai" + current date
- Center card: Large "+ Start Deep Work" CTA button (full-width, indigo gradient)
- Below CTA:
  - Input field: "What are you working on?" (goal/task name)
  - Input field: "Category" with dropdown autocomplete (recent categories as chips below)
  - Input field: "Tag" optional
- Quick-start chips row: 3–4 recent goals as tappable chips that auto-fill the goal input
- Bottom section: "Category suggestions" — 2–3 auto-suggested rules based on time of day + keyword matching, shown as suggestion cards with icon + category name + reason text ("Based on keyword 'design'", "Typical for 10:00–12:00")

### Active Session State

- Full screen takeover with dark background (#0D0F14)
- Center: Giant monospace elapsed timer "01:23:47" in 64sp font with subtle indigo glow
- Below timer: Goal title in white/20sp, category badge pill
- Stats row: interruption count icon + count, focus quality percentage
- Bottom: Full-width red "End Session" button with subtle pulse animation
- Small FAB (top-right): "Interrupt" button to manually log a distraction

---

## SCREEN 2 — Todo List

**Route**: Bottom tab 2, checklist icon

- Top app bar: "Tasks" title + "+" icon button (top right)
- Filter chips row: All / Active / Done + Sort: Name / Date / Goal
- List items (each todo card, 72dp height):
  - Left: Circular checkbox — animated checkmark on complete
  - Center: Title (strikethrough when done), Goal badge pill (indigo), Due date (red if overdue)
  - Left border: 4dp colored stripe — red = high priority, yellow = medium, transparent = low
  - Swipe left to delete
- Empty state: Minimal centered illustration + "No tasks yet. Add your first task."
- FAB: "+" floating button (indigo, bottom-right, 56dp)

### Add / Edit Todo — Bottom Sheet

- Drag handle at top
- Title input (large, 20sp, autofocus)
- Description multiline input (gray, 14sp)
- Goal picker row: dropdown icon + recent session goals listed
- Priority selector: Low / Medium / High segmented toggle
- Due date picker: tap to open date picker dialog
- Status selector: TODO / IN PROGRESS / PAUSED / DONE chips
- Full-width "Save" button (indigo)

### Todo Detail Screen

- Top app bar: back arrow + "Edit" text button (top right)
- Todo title (24sp, bold)
- Row: Status badge + Priority badge
- Goal chip (indigo, tappable)
- Description section (gray card)
- Timestamps section: Created, Updated, Completed (monospace, 12sp)
- Bottom: Full-width "Delete" button (red, destructive)

---

## SCREEN 3 — Dashboard

**Route**: Bottom tab 3, chart icon

### Today Stats Card (top, prominent, indigo gradient border)

2×2 metric grid inside card:
- Focus Time: "3h 42m" (large monospace, 28sp)
- Sessions: "4"
- Avg Duration: "55m"
- Best Hour: "10:00"

### Focus Timeline Bar Chart

- Scrollable horizontal bar chart
- X-axis: hours of today 0–23
- Each bar represents a session, colored by category
- Tap bar: tooltip shows goal name + duration
- Empty hours: faint dotted line

### Goal Distribution Donut Chart

- Donut chart, 200dp diameter
- Each segment = one category, color-coded
- Center label: total focus time today (monospace)
- Right-side legend: category name + duration + color dot
- Tap segment → navigate to Goal Detail

### Recent Sessions List

- Section title: "Recent Sessions" + "See all" link
- Each row: goal name (14sp), duration pill (indigo), time-ago label (gray), category badge
- Active session row: pulsing green dot before goal name
- Divider between rows

---

## SCREEN 4 — Goals / Category Screen

**Route**: Bottom tab 4, star icon

- Top app bar: "Goals" title + "+ New Goal" icon button
- Each goal card (80dp):
  - Goal name (16sp, bold)
  - Horizontal progress bar showing share of total focus time (indigo fill)
  - Share % label (right, 12sp, gray)
  - Week-over-week delta: green "↑ 12%" or red "↓ 8%" (12sp)
  - Chevron right to enter detail
- Pull-to-refresh supported

### Category Detail Screen

- Top app bar: back arrow + category name as title
- Top metrics row — 4 cards horizontally scrollable:
  - Efficiency % (focused/total ratio)
  - Coverage % (active days / total days)
  - Current Streak (days)
  - Best Streak (days)
- Time range tab selector: 30D | 12W | 12M
- Bar chart (full width, updates per selected range)
- "Focus Heatmap" section:
  - 7 rows (Mon–Sun) × 24 columns (0–23h)
  - Each cell: opacity of indigo block represents intensity
  - X-axis labels: 0, 6, 12, 18, 23
  - Y-axis labels: Mon, Tue, Wed, Thu, Fri, Sat, Sun
- "Auto-assign Rules" section:
  - Each rule card: keyword tag + hour range badge + priority number
  - Swipe to delete
  - "+ Add Rule" FAB (bottom-right)

---

## SCREEN 5 — Profile

**Route**: Bottom tab 5, person icon

- Profile header card:
  - Avatar: 64dp circle with user initials (indigo background)
  - Name (20sp, bold)
  - Subtitle: "Knowledge Worker" (gray, 14sp)
- Stats summary row (3 metrics):
  - Total Sessions (monospace)
  - Total Focus Hours (monospace)
  - Longest Streak (monospace)
- "Insights" section:
  - Each insight card: icon + bold insight title + description text
  - Examples: "Peak hour: 10:00–11:00", "Avg session length: 52 min", "Best day: Tuesday"
  - Indigo left border accent on each card
- "Settings" section (list items):
  - Notifications (toggle)
  - Export Data (arrow, coming soon)
  - Theme (Light / Dark / System toggle)
  - App version (gray, 12sp, bottom)

---

## NAVIGATION STRUCTURE

- Bottom navigation bar: 5 tabs — Session (clock), Todo (checklist), Dashboard (chart bar), Goals (star), Profile (person)
- Bottom bar is hidden on push detail screens: CategoryDetail, GoalDetail, TodoDetail
- Active tab indicator: 3dp indigo underline below icon
- Tab transitions: fade for sibling tabs, slide-right for push/pop

---

## ADDITIONAL SPECS

- Status bar: transparent, light-colored icons (dark mode default)
- Safe area: insets respected for notch and home indicator
- Pull-to-refresh: on Dashboard and Goals screens
- Empty states: each major list has a centered minimal empty state with icon + short message
- Loading states: skeleton shimmer screens (pulsing gray blocks matching card layout), not spinners
- Error states: inline red banner with message + "Retry" text button
- Accessibility: minimum 44dp touch targets, WCAG AA contrast on all text
- Bottom sheet inputs: keyboard push-up behavior, avoid content obscured
