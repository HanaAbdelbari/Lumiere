# Lumière — Design System (V1)

The look: white-led, elegant, feminine. The product is the hero; the interface
stays quiet. Clean white space reads as luxury. Beige is almost gone from
backgrounds — it survives only as a barely-visible hairline border. Gold is a
thin metallic touch (lines, small details), rose is a small feminine touch
(sale, links, heart). Neither is ever a background.

**Color balance:** ~80% white · 10% brown (important elements) · 5% beige
(hairline borders only) · 3% gold (metallic touches) · 2% dusty rose (feminine
touches).

---

## Colors

| Token        | Hex       | Role                                             |
| ------------ | --------- | ------------------------------------------------ |
| white        | `#FFFFFF` | Primary background — pages, sections, cards, image backgrounds |
| off-white    | `#FCFCFC` | Optional soft page background if pure white feels too stark |
| border       | `#EFE8DF` | Hairline borders around cards and under images — barely visible |
| brown        | `#4E342E` | Logo, headings, body emphasis, primary buttons, icons |
| brown-soft   | `#8A6D52` | Secondary text, prices                           |
| muted        | `#B0A08F` | Struck-through old prices, captions, disabled text |
| gold         | `#C6A15B` | Thin accent lines, small metallic details, hover — NEVER a fill area |
| rose         | `#C89CA6` | Sale badge, links, active state, heart icon — small touches only |
| rose-text    | `#5A2E38` | Text placed on a rose background (badges)         |

Rules:
- White dominates. Sections are white; beige is not a section background.
- Gold is a line or a detail, never a filled area (gold fills read as dated).
- Rose appears in one or two small places per screen, never more.
- Text on a colored background uses the darkest shade of that same family
  (e.g. rose-text on a rose badge), never plain black.

---

## Typography

Two families:

- **Cormorant Garamond** (serif) — headings, product names, section titles.
  Gives the elegant, timeless feel.
- **Inter** (or Poppins) — body text, buttons, captions, prices. Clean and
  highly readable.

| Style     | Font      | Size  | Use                          |
| --------- | --------- | ----- | ---------------------------- |
| Hero      | Cormorant | 28–32 | Hero headline                |
| Heading 1 | Cormorant | 20–24 | Section titles               |
| Heading 2 | Cormorant | 16–18 | Product name, card titles    |
| Body      | Inter     | 13–15 | Descriptions, general text   |
| Caption   | Inter     | 11–12 | Small supporting text        |
| Button    | Inter     | 12–14 | Button labels                |

Always sentence case — never ALL CAPS for content (small letter-spaced labels
like the logo are the only exception).

---

## Accent line

A recurring signature detail: a short thin gold line (≈36px wide, 1.5px tall,
`#C6A15B`) centered under section titles. Small, but it's what makes the brand
feel considered.

---

## Border radius

| Element  | Radius |
| -------- | ------ |
| Cards    | 10px   |
| Buttons  | 6px    |
| Inputs   | 6px    |
| Images   | 8px    |
| Badges   | 12px (pill) |

---

## Spacing scale

`4 · 8 · 12 · 16 · 24 · 32 · 48 · 64` — use these steps only, for padding,
margins, and gaps. Consistent spacing is a big part of the calm, premium feel.

---

## Buttons

| Variant   | Look                                              | Use                    |
| --------- | ------------------------------------------------- | ---------------------- |
| Primary   | Brown fill (`#4E342E`), white text                | Add to Cart, Place Order |
| Outline   | Brown border, brown text, white fill              | Shop Now, secondary    |
| Ghost     | No border, rose text (`#C89CA6`), arrow →         | View all, inline links |
| Disabled  | Beige fill (`#EADBC8`), muted text                | Inactive Place Order   |

---

## Product card

The most reused component. Appears in Shop, Sale, New Arrivals, related products.

- White background, hairline border (`#EFE8DF`), 10px radius.
- Image on white background, thin border underneath.
- Product name in Cormorant, brown.
- Price in Inter, brown. If on sale: sale price in brown + old price struck
  through in muted, plus a rose `-XX%` badge on the image.

---

## Badges

| Badge         | Background | Text color |
| ------------- | ---------- | ---------- |
| Sale −XX%     | rose       | rose-text  |
| New           | beige      | brown-soft |
| Out of stock  | off-white  | muted      |
| Free shipping | brown      | white      |

---

## Toasts

Success, error, warning, info — small, top of screen, auto-dismiss. Used after
Add to Cart ("Added to cart") with Continue Shopping / View Cart actions.

---

## Tailwind config (for implementation)

Define these once in `tailwind.config.js` so the whole site uses named colors
and a single change updates everywhere — same "single source of truth"
principle as the database.

```js
theme: {
  extend: {
    colors: {
      brown:      '#4E342E',
      'brown-soft': '#8A6D52',
      muted:      '#B0A08F',
      gold:       '#C6A15B',
      rose:       '#C89CA6',
      'rose-text': '#5A2E38',
      sand:       '#EADBC8',
      hairline:   '#EFE8DF',
    },
    fontFamily: {
      serif: ['Cormorant Garamond', 'serif'],
      sans:  ['Inter', 'sans-serif'],
    },
  },
}
```
