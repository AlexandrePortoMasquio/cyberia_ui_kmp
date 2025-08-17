# Interface KMM para Cyberia

Este projeto é um esqueleto de interface Web em Kotlin Multiplatform (destino JS) para integrar com o contrato de escrow **Cyberia** na Solana. O nome do projeto no Gradle foi ajustado para `cyberia_ui_kmp` e pode ser modificado conforme necessário.

## Conteúdo

- `webApp` — módulo Kotlin/JS com a interface web e integração com Phantom/Solana.
- `shared` — módulo multiplataforma vazio, destinado a compartilhamento de lógica entre plataformas futuras (Android/iOS).

## Como rodar localmente

Requisitos: JDK 17+, Node 18+.

```bash
./gradlew :webApp:browserDevelopmentRun
```

O servidor de desenvolvimento abrirá automaticamente a interface em seu navegador. A conexão RPC padrão aponta para a `Devnet`. Você pode trocar definindo a variável de ambiente `SOLANA_RPC` antes de rodar.

## Inicializando um repositório Git

Para versionar este projeto em um novo repositório chamado **cyberia_ui_kmp** no GitHub, siga os passos abaixo no terminal:

```bash
cd cyberia_kmm_ui              # navegue até o diretório do projeto
git init                      # inicializa um repositório git vazio
git checkout -b develop       # cria a branch principal de desenvolvimento (opcional)
git add .                     # adiciona todos os arquivos
git config user.name "Seu Nome"      # configure seu nome de autor
git config user.email "seu@email"    # configure seu e-mail de autor
git commit -m "Primeiro commit: estrutura KMM"

# adicione o repositório remoto (ajuste a URL para o seu repositório)
git remote add origin https://github.com/AlexandrePortoMasquio/cyberia_ui_kmp.git

# envie sua branch para o GitHub
git push -u origin develop
```

Depois de enviar o código, você pode criar branches de funcionalidade (`feature/...`) e abrir Pull Requests para a branch `develop`, conforme seu fluxo de trabalho.
