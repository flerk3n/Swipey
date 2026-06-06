# Swipey Design Specification (Jetpack Compose / Material 3)

## 1. GLOBAL DESIGN TOKENS

### Color Palette (Hex Codes)
| Category | Token | Hex Code | Usage |
| :--- | :--- | :--- | :--- |
| **Primary** | `primary` | `#C6FF33` | Brand accent, active states, key icons |
| **Secondary / CTA** | `secondary` | `#C6FF33` | Primary action buttons (solid lime) |
| **Surface** | `surface` | `#000000` | Main background (Pure Black) |
| **On-Surface** | `onSurface` | `#FFFFFF` | Primary text and headings |
| **Surface Variant** | `surfaceVariant` | `#1D2111` | Glassmorphic card backgrounds (with opacity) |
| **Outline** | `outline` | `#373B2D` | Borders, dividers, and inactive states |

### Typography (Material 3 Alignment)
| Role | Font Family | Size (sp) | Weight | Style |
| :--- | :--- | :--- | :--- | :--- |
| **Headline Large** | Instrument Serif | 32sp | 700 (Bold) | Italic |
| **Title Medium** | Instrument Serif | 22sp | 600 (Semi-Bold) | Normal |
| **Body Large** | Instrument Sans | 16sp | 400 (Regular) | Normal |
| **Label Large** | Instrument Sans | 14sp | 500 (Medium) | Muted |

### Spacing & Shapes
- **Layout Margins**: 24dp (horizontal), 32dp (vertical)
- **Section Spacing**: 24dp
- **Card Corner Radius**: 24dp (Large), 16dp (Medium)
- **Button Corner Radius**: 100dp (Full Round / Pill)
- **Input Corner Radius**: 12dp

---

## 2. SCREEN REFERENCE GALLERY

| Technical Title | Purpose | Structural Priority |
| :--- | :--- | :--- |
| `WelcomeSplash` | First-time user onboarding and brand entry | Visual-heavy, floating elements |
| `NicheSelection` | Step 1: Broad category choice | Progress-tracked, list-based |
| `TopicSelection` | Step 2: Specific sub-niche tags | Chip/Tag cloud interaction |
| `GoalSetting` | Step 3: Frequency & volume commitment | High-intent card selection |
| `Dashboard` | Main activity hub for swiping and saved ideas | Content feed, progress tracking |
| `Profile` | User settings and account overview | Form-based, integrated services |
| `Settings` | Integration management (Notion, Google Calendar) | List-based toggles and status |

---

## 3. COMPONENT-LEVEL SPECIFICATIONS

### TopAppBar (Small)
- **Container**: `Scaffold` TopBar slot.
- **Layout**: `CenterAlignedTopAppBar` or `Box` with `Row`.
- **Styling**: Background `Color.Transparent`, Title `Headline Large` (Italic).

### BottomNavBar
- **Container**: `NavigationBar` (Fixed bottom).
- **Layout**: `Row` with `Arrangement.SpaceAround`.
- **Styling**: `SurfaceContainerLowest`, rounded pill container floating 8dp from bottom.

### Selection Cards (Glassmorphic)
- **Container**: `Box` with `border` and `background`.
- **Selected State**: `background: Color(0x14C6FF33)` (8% Lime), `border: 1dp Solid White`, Left-edge accent: `4dp Lime Glow`.
- **Unselected State**: `background: Color(0x0DFFFFFF)` (5% White), `border: 1dp Solid Outline`.

### Custom CTA Button
- **Container**: `Button`.
- **Layout**: `fillMaxWidth()`, `height(56.dp)`.
- **Styling**: `colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF33))`. Content: `Color.Black`.

---

## 4. FIXED IMAGE ASSET REGISTRY

| Asset Token | Description                                       | Format |
| :--- |:--------------------------------------------------| :--- |
| `LOGO_MAIN` | Swipey icon inside resources folder               | Vector (SVG) |
| `ONBOARDING_HERO` | Floating 3D abstract shapes with neon gradients   | Raster (High-Res) |
| `ICON_CALENDAR` | Google Calendar integration logo                  | Vector |
| `ICON_NOTION` | Notion integration logo                           | Vector |
| `TREND_BG_ABSTRACT` | Futuristic digital art for trend card backgrounds | Raster |

---

## 5. AI IMPLEMENTATION SPECIAL INSTRUCTIONS

### Architecture & Patterns
- **Pattern**: Strictly follow **MVVM** with **State-driven UI**. Use `MutableStateFlow` in ViewModels to drive Compose Recomposition.
- **Theme**: Extend `MaterialTheme` using the tokens defined in Section 1.

### UI Logic Rules
- **CTA Buttons**: Must always be `#C6FF33` Lime with Black text. No gradients unless specified.
- **Backgrounds**: Use `#252736` (grey-Black) as the primary window background to ensure high contrast for the Lime accents.
- **Glassmorphism**: Implement card backgrounds using `SurfaceVariant` with low alpha (0.05 - 0.1) and a subtle 1dp border.
- **Animations**: Use `AnimatedVisibility` for card selection transitions and `Modifier.graphicsLayer` for floating effects on the splash screen.
