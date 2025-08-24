# KMM Interface for Cyberia

This project is a Kotlin Multiplatform (JS target) web interface skeleton to integrate with the Cyberia escrow contract on Solana. The Gradle project name is set to `cyberia_ui_kmp` and can be changed as needed.

## Contents

- `webApp` — Kotlin/JS module with the web UI and Phantom/Solana integration.
- `shared` — empty multiplatform module, intended for sharing logic across future platforms (Android/iOS).

## Running Locally

Requirements: JDK 17+, Node 18+.

```bash
./gradlew :webApp:browserDevelopmentRun
```

The development server will automatically open the interface in your browser. The default RPC connection points to `Devnet`. You can change it by setting the `SOLANA_RPC` environment variable before running.

## Initializing a Git Repository

To version this project in a new repository named **cyberia_ui_kmp** on GitHub, follow the steps below in the terminal:

```bash
cd cyberia_ui_kmp             # navigate to the project directory
git init                      # initialize an empty git repository
git checkout -b develop       # create the main development branch (optional)
git add .                     # add all files
git config user.name "Your Name"       # set your author name
git config user.email "your@email"     # set your author email
git commit -m "First commit: KMM structure"

# add the remote repository (adjust the URL to your repo)
git remote add origin https://github.com/AlexandrePortoMasquio/cyberia_ui_kmp.git

# push your branch to GitHub
git push -u origin develop
```

After pushing the code, you can create feature branches (`feature/...`) and open Pull Requests to the `develop` branch according to your workflow.
