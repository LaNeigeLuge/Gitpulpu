# Gitpulpu

A multiplatform Git client built with Jetpack Compose Desktop and JGit.
Fork of [Gitnuro](https://github.com/JetpackDuba/Gitnuro) with a redesigned UI, smarter error handling, and quality-of-life improvements.

![Gitpulpu](icons/gitpulpu%20look.png)

## What's different from Gitnuro

- **Caldera-inspired theme** — warm dark mode with glow-line graph, concrete light mode with gradient background and floating parchment cards
- **Auto-stash checkout** — switching branches with uncommitted changes automatically stashes, checks out, and pops. No more conflict dialogs for simple branch switches
- **Friendly error messages** — known errors (merge conflicts, auth failures, ref collisions) show actionable guidance instead of raw stack traces
- **Syntax highlighting fix** — resolved a crash (StackOverflowError) when viewing `.tfvars` and other files with long strings

## Install

**Linux:**

```bash
# From source
git clone https://github.com/jujuroro93/Gitpulpu.git
cd Gitpulpu
./gradlew run
```

**Windows / macOS:**

```bash
git clone https://github.com/jujuroro93/Gitpulpu.git
cd Gitpulpu
./gradlew packageDistributionForCurrentOS
```

Requires **JDK 17+**.

## Build

```bash
# Run in dev mode
./gradlew run

# Package for distribution
./gradlew packageDistributionForCurrentOS

# Build JAR (portable, requires JRE 17)
./gradlew packageUberJarForCurrentOS
```

## Features

Everything from Gitnuro, plus the improvements above:

- View diffs for text-based files with syntax highlighting
- Full commit history with branch graph
- Stage, unstage, and discard changes (files, hunks, or individual lines)
- Commit, amend, revert, cherry-pick, reset
- Branch management (create, delete, rename, checkout)
- Merge and rebase (including interactive rebase)
- Pull, push, force push, fetch
- Stash and pop stash
- Tag management
- Remote management
- Clone repositories
- Submodule support
- File blame and file history
- Side-by-side diff view
- Image diff (side-by-side comparison)
- Search by commit message, author, or commit ID
- Custom JSON themes

## Custom themes

Gitpulpu supports custom themes in JSON format via Settings. Example:

```json
{
  "primary": "FFFC5000",
  "primaryVariant": "FFFF8A50",
  "onPrimary": "FFFFFFFF",
  "secondary": "FF7B74FF",
  "onSecondary": "FFFFFFFF",
  "onBackground": "FFF0EFEB",
  "onBackgroundSecondary": "FFB0AEA8",
  "error": "FFD03030",
  "onError": "FFFFFFFF",
  "background": "FF121214",
  "backgroundSelected": "FF3D2C15",
  "surface": "FF1C1C20",
  "secondarySurface": "FF262630",
  "tertiarySurface": "FF2E2822",
  "addFile": "FF2E8C48",
  "deletedFile": "FFD03030",
  "modifiedFile": "FFFC5000",
  "conflictingFile": "FFD88020",
  "dialogOverlay": "AA000000",
  "normalScrollbar": "FF555550",
  "hoverScrollbar": "FFFC5000",
  "diffLineAdded": "AA3A5038",
  "diffLineRemoved": "AA5A3838",
  "diffContentAdded": "4530A030",
  "diffContentRemoved": "45C03030",
  "diffKeyword": "FFA0CAF0",
  "diffAnnotation": "FFD0CC60",
  "diffComment": "FF70C290",
  "isLight": false
}
```

Colors are in ARGB hex format.

## Contributing

Issues and pull requests are welcome. For new features, please open an issue first to discuss the approach.

See [DEVELOPMENT.md](DEVELOPMENT.md) for development setup.

## Credits

Built on [Gitnuro](https://github.com/JetpackDuba/Gitnuro) by JetpackDuba.

## License

Licensed under the same terms as the original Gitnuro project.
