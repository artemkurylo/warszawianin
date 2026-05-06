# warszawianin

Snap a photo of a neighbourhood problem, let AI write the report, send it to Warsaw 19115.

## Features

- **AI-generated reports** — Gemini 1.5 Flash analyses your photo and produces a Polish-language title, category and description
- **One-tap submit** — pre-fills an email to `kontakt@um.warszawa.pl` with the photo attached
- **Your reports** — locally stored ticket list (Room) with draft / sent status
- **Neighbourhood feed** — see mock reports from nearby residents ("W okolicy" tab); shows what problems neighbours have already flagged in a 500 m radius

## Neighbourhood Feed (mock data)

The "W okolicy" tab ships with 6 hardcoded neighbour reports to demonstrate the community feature:

| # | Title | Category | Author |
|---|-------|----------|--------|
| 1 | Porzucony rower blokuje wejście | infrastruktura | Anna K. |
| 2 | Graffiti na bramie kamienicy | czystość | Tomasz W. |
| 3 | Uszkodzony kosz na śmieci | czystość | Maria S. |
| 4 | Głęboka dziura przy przejściu dla pieszych | drogi | Piotr M. |
| 5 | Niedziałająca latarnia uliczna | oświetlenie | Karolina B. |
| 6 | Połamane drzewo zwisa nad chodnikiem | zieleń | Robert J. |

See [IDEA.md](IDEA.md) for full architecture, wireframes, data models and task list.

