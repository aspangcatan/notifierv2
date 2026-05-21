---
name: Clinical Precision
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#424752'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#727783'
  outline-variant: '#c2c6d4'
  surface-tint: '#005db6'
  primary: '#00478d'
  on-primary: '#ffffff'
  primary-container: '#005eb8'
  on-primary-container: '#c8daff'
  inverse-primary: '#a9c7ff'
  secondary: '#006970'
  on-secondary: '#ffffff'
  secondary-container: '#7af1fc'
  on-secondary-container: '#006e75'
  tertiary: '#43484c'
  on-tertiary: '#ffffff'
  tertiary-container: '#5b6063'
  on-tertiary-container: '#d6dade'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d6e3ff'
  primary-fixed-dim: '#a9c7ff'
  on-primary-fixed: '#001b3d'
  on-primary-fixed-variant: '#00468c'
  secondary-fixed: '#7df4ff'
  secondary-fixed-dim: '#5dd8e2'
  on-secondary-fixed: '#002022'
  on-secondary-fixed-variant: '#004f54'
  tertiary-fixed: '#dfe3e7'
  tertiary-fixed-dim: '#c3c7cb'
  on-tertiary-fixed: '#171c1f'
  on-tertiary-fixed-variant: '#43474b'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-bold:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  patient-code:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 22px
    letterSpacing: 0.02em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  margin-mobile: 16px
  margin-desktop: 48px
  gutter: 16px
---

## Brand & Style
The design system is engineered for high-stakes medical environments where speed of information retrieval and absolute clarity are paramount. The brand personality is authoritative yet calm, functioning as a reliable tool for healthcare professionals. 

The aesthetic follows a **Corporate / Modern** approach with a heavy emphasis on **Functional Minimalism**. Every visual element is scrutinized for its utility; whitespace is used strategically to reduce cognitive load during high-pressure clinical workflows. The interface must evoke a sense of stability and professional rigor, ensuring that practitioners feel confident in the data integrity and the system's responsiveness.

## Colors
The palette is rooted in established medical semiotics. **Primary Blue** (#005EB8) serves as the anchor for navigation and primary actions, signaling trust and institutional stability. **Healthcare Teal** (#00A3AD) is utilized for secondary supportive actions and specialized data visualizations.

The background architecture uses a tiered grayscale system:
- **Surface:** Pure White (#FFFFFF) for primary content cards and input areas.
- **Background:** Light Gray (#F8FAFC) to provide subtle contrast for card boundaries.
- **Status Colors:** High-saturation tokens for 'Done' (Green), 'Queued' (Amber), and 'Emergency' (Red) to ensure immediate peripheral recognition of patient priority.

## Typography
This design system utilizes **Inter** for its exceptional legibility and neutral, systematic tone. The type hierarchy is strictly defined to prioritize critical data points:
- **Patient Codes and Ages** use the `patient-code` or `headline-md` tokens with high contrast (Slate-900) to ensure they are readable at a glance.
- **Labels** for metadata (e.g., "Department", "Time Elapsed") use all-caps with increased letter spacing to distinguish them from dynamic patient data.
- **Mobile adjustments:** Headlines scale down to a maximum of 24px on mobile devices to maintain information density without sacrificing clarity.

## Layout & Spacing
The layout follows a **8px square grid system** to ensure consistent vertical rhythm. On mobile, a single-column fluid layout is used with 16px side margins. 

**Referral Cards** should use 16px internal padding (`spacing.md`) to maintain a breathable but dense information environment. Data-heavy lists may reduce vertical padding to 12px to increase the number of visible records on screen. For tablet and desktop, a 12-column grid is employed, allowing patient details to appear in a side-panel (6 columns) while the list remains active (6 columns).

## Elevation & Depth
This design system avoids heavy shadows to prevent visual "muddiness." Instead, it uses **Tonal Layers** and **Low-contrast Outlines**:
- **Level 0 (Background):** #F8FAFC.
- **Level 1 (Cards):** White background with a 1px border of #E2E8F0. 
- **Level 2 (Active/Floating):** Use a very soft, diffused shadow (0px 4px 12px rgba(0, 0, 0, 0.05)) for Floating Action Buttons or active Bottom Sheets to separate them from the content layer.
- **Depth through Color:** Critical alerts use a subtle background tint of their status color (e.g., Emergency cards may have a 5% Red fill) to create depth without adding shadow complexity.

## Shapes
A **Soft** (Level 1) roundedness profile is applied across the system. This provides a modern, approachable feel while maintaining the structured, professional look required for a clinical tool.
- **Standard Elements:** 0.25rem (4px) for input fields and small buttons.
- **Cards:** 0.5rem (8px) for Referral Cards.
- **Status Badges:** Fully rounded (pill-shaped) to distinguish them from interactive buttons.

## Components
- **Referral Cards:** Must include a clear header containing `Hospital Name` and `Date`. The `Patient Code` should be positioned in the top right or left with maximum visual weight.
- **Status Badges:** Use a "Light Mode" aesthetic—saturated text on a 10-15% opacity background of the same hue (e.g., Green text on light green background).
- **Primary Actions:** Floating Action Buttons (FAB) should be reserved for the most frequent clinical actions, like "New Referral." Secondary actions like "Forward" or "Queue" should exist within Bottom Sheets or as clear outlined buttons within cards.
- **Segmented Tabs:** Use a container-based toggle for switching between 'Referrals' and 'Queue,' utilizing the Primary Blue for the active state background with white text.
- **Filtering UI:** Department filters should be presented as a horizontal scroll of chips or a clear dropdown menu with "Selected" states using the Healthcare Teal color.
- **Input Fields:** Use a 1px border (#CBD5E1) that thickens and changes to Primary Blue on focus. Labels must remain visible above the field at all times.