# 🦷 SmileLab

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![SDK](https://img.shields.io/badge/Min%20SDK-28-green.svg)
![SDK](https://img.shields.io/badge/Target%20SDK-36-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024-blue.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

**Aplicativo educativo Android para saúde bucal e higiene oral**

[Características](#-características) •
[Screenshots](#-screenshots) •
[Arquitetura](#-arquitetura) •
[Instalação](#-instalação) •
[Tecnologias](#-tecnologias) •
[Contribuir](#-contribuir) •
[Licença](#-licença)

</div>

---

## 📱 Sobre o Projeto

**SmileLab** é um aplicativo educativo Android moderno, desenvolvido com **Jetpack Compose** e **Material Design 3**, que oferece informações abrangentes sobre saúde bucal, higiene oral e cuidados dentários. O app combina conteúdo educativo de qualidade com visualização 3D interativa de modelos dentários usando **Google Filament**.

### 🎯 Objetivo

Educar usuários sobre:
- ✅ Anatomia dentária e tipos de dentes
- ✅ Técnicas corretas de escovação e uso do fio dental
- ✅ Problemas dentários comuns e sua prevenção
- ✅ Rotinas de higiene bucal saudáveis
- ✅ Lembretes personalizados para escovação

> ⚠️ **Aviso**: Este é um aplicativo educativo e **não substitui** consulta com profissionais de odontologia.

---

## ✨ Características

### 🏠 Módulos Educativos

- **Conheça Seus Dentes** - Anatomia dental, tipos de dentes e suas funções
- **Higiene Bucal** - Guias detalhados de escovação, uso do fio dental e limpeza da língua
- **Problemas Dentários** - Informações sobre cáries, gengivite, placas e mais
- **Rotina & Hábitos** - Construa uma rotina de saúde bucal eficaz

### 🦷 Visualização 3D

- Renderização 3D interativa de modelos dentários com **Google Filament**
- Suporte para arquivos `.gltf` e `.glb`
- Controles de câmera intuitivos (rotação, zoom, pan)
- Performance otimizada com thread GL dedicada

### 🔔 Sistema de Lembretes

- Lembretes personalizáveis para escovação
- Múltiplos horários por dia
- Seleção de dias da semana
- Notificações locais

### 🎨 Interface Moderna

- **Material Design 3** com tema personalizado
- **Jetpack Compose** para UI declarativa e reativa
- Animações fluidas e transições suaves
- Modo escuro (em desenvolvimento)
- Design responsivo e acessível

---

## 📸 Screenshots

<div align="center">
  <img src="docs/screenshots/splash.png" width="200" alt="Splash Screen" />
  <img src="docs/screenshots/home.png" width="200" alt="Home Screen" />
  <img src="docs/screenshots/3d_viewer.png" width="200" alt="3D Viewer" />
  <img src="docs/screenshots/guides.png" width="200" alt="Hygiene Guides" />
</div>

---

## 🏗️ Arquitetura

O SmileLab segue as **melhores práticas do Android moderno**:

### 📐 Padrão Arquitetural

```
┌─────────────────────────────────────────────┐
│              UI Layer (Compose)              │
│  ┌─────────────┐  ┌──────────────────────┐ │
│  │  Screens    │  │  Components          │ │
│  └─────────────┘  └──────────────────────┘ │
└─────────────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────┐
│           Domain Layer (Optional)            │
│  ┌─────────────┐  ┌──────────────────────┐ │
│  │  Use Cases  │  │  Business Logic      │ │
│  └─────────────┘  └──────────────────────┘ │
└─────────────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────┐
│              Data Layer                      │
│  ┌─────────────┐  ┌──────────────────────┐ │
│  │  Repository │  │  Local Data (Room)   │ │
│  └─────────────┘  └──────────────────────┘ │
└─────────────────────────────────────────────┘
```

### 📂 Estrutura de Pastas

```
com.cleansoft.smilelab/
├── data/
│   ├── local/
│   │   ├── dao/              # Data Access Objects
│   │   ├── entity/           # Room Entities
│   │   └── converter/        # Type Converters
│   └── repository/           # Repositórios de dados
├── filament/
│   ├── FilamentEngineManager.kt   # Singleton do Filament Engine
│   ├── FilamentSceneManager.kt    # Gerenciador de cena 3D
│   ├── ModelLoader.kt             # Carregador de modelos GLTF/GLB
│   └── ResourceManager.kt         # Gerenciador de assets
├── navigation/
│   ├── Screen.kt             # Definições de rotas
│   └── SmileLabNavGraph.kt   # Grafo de navegação
├── ui/
│   ├── components/           # Componentes reutilizáveis
│   │   └── FilamentViewer3D.kt
│   ├── screens/              # Telas do app
│   │   ├── home/
│   │   ├── viewer3d/
│   │   ├── hygiene/
│   │   ├── problems/
│   │   └── reminders/
│   └── theme/                # Material Design 3 Theme
├── MainActivity.kt
└── SmileLabApplication.kt
```

### 🔧 Componentes Principais

#### Filament 3D Engine

- **FilamentEngineManager**: Singleton thread-safe para gerenciar o ciclo de vida do Engine
- **FilamentSceneManager**: Orquestra cena, câmera, renderização e assets
- **ModelLoader**: Carregamento assíncrono de modelos 3D com thread GL dedicada
- **ResourceManager**: Gerenciamento de assets e memória

#### Database (Room)

- **SmileLabDatabase**: Banco de dados local com Room
- **DAOs**: UserProgressDao, BrushingReminderDao, DentalContentDao
- **Entities**: UserProgress, BrushingReminder, DentalContent

#### Navigation

- **Jetpack Navigation Compose**: Navegação type-safe e declarativa
- **Deep Links**: Suporte para navegação profunda
- **Back Stack Management**: Gerenciamento automático de pilha de navegação

---

## 🚀 Instalação

### Pré-requisitos

- **Android Studio**: Ladybug | 2024.2.1 ou superior
- **JDK**: 11 ou superior
- **Gradle**: 8.7 ou superior
- **Min SDK**: 28 (Android 9.0 Pie)
- **Target SDK**: 36 (Android 15)

### Clone o Repositório

```bash
git clone https://github.com/duartegauss/smilelab.git
cd smilelab
```

### Configuração

1. **Abra o projeto no Android Studio**
2. **Sincronize o Gradle**: `File > Sync Project with Gradle Files`
3. **Configure o emulador ou dispositivo físico**
4. **Execute o app**: `Run > Run 'app'` ou pressione `Shift + F10`

### Build do APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requer configuração de signing)
./gradlew assembleRelease
```

O APK será gerado em: `app/build/outputs/apk/`

---

## 🛠️ Tecnologias

### Core

- **[Kotlin](https://kotlinlang.org/)** `2.1.0` - Linguagem de programação
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** `2024.12.00` - UI declarativa
- **[Material Design 3](https://m3.material.io/)** - Design system

### Jetpack

- **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)** `2.8.5` - Navegação
- **[Room](https://developer.android.com/training/data-storage/room)** `2.6.1` - Banco de dados local
- **[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)** `1.1.1` - Armazenamento de preferências
- **[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)** `2.10.0` - Tarefas em background
- **[Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle)** `2.8.7` - Componentes lifecycle-aware

### 3D Rendering

- **[Google Filament](https://github.com/google/filament)** `1.54.3` - Engine de renderização 3D
- **[GLTF I/O](https://google.github.io/filament/gltfio.html)** - Carregamento de modelos GLTF/GLB

### Coroutines

- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** `1.9.0` - Programação assíncrona
- **[Coroutines Android](https://developer.android.com/kotlin/coroutines)** - Integrações Android

### Build & Tools

- **[Gradle](https://gradle.org/)** `8.7` - Build system
- **[KSP](https://github.com/google/ksp)** `2.1.0-1.0.29` - Kotlin Symbol Processing
- **[Gradle Version Catalog](https://docs.gradle.org/current/userguide/platforms.html)** - Gerenciamento de dependências

---

## 📋 Funcionalidades Detalhadas

### 🏠 Tela Inicial

- Cards de módulos educativos com navegação rápida
- Acesso ao visualizador 3D
- Atalhos para lembretes
- Banner informativo com disclaimer

### 🦷 Conheça Seus Dentes

- Tipos de dentes: incisivos, caninos, pré-molares, molares
- Anatomia dental: esmalte, dentina, polpa, raiz
- Funções e características de cada tipo
- Informações sobre dentição permanente e decídua

### 🪥 Guias de Higiene

#### Escovação
- Passo a passo detalhado (8 etapas)
- Tempo recomendado por área
- Técnicas corretas de movimento
- Erros comuns a evitar

#### Fio Dental
- Técnica correta de uso
- Quantidade ideal de fio
- Movimento em forma de C
- Alternativas (fita dental, escovas interdentais)

#### Limpeza da Língua
- Importância da limpeza lingual
- Ferramentas disponíveis
- Técnica correta
- Benefícios para saúde bucal

### 🚨 Problemas Dentários

Informações sobre:
- Cáries (causas, sintomas, prevenção)
- Gengivite e periodontite
- Placas bacterianas
- Sensibilidade dental
- Mau hálito (halitose)
- Erosão ácida

Cada problema inclui:
- ⚠️ Badge de severidade
- 📋 Descrição detalhada
- 🔍 Causas principais
- 🩺 Sintomas comuns
- ✅ Métodos de prevenção

### 🔔 Sistema de Lembretes

- Criar múltiplos lembretes
- Configurar horário (hora e minuto)
- Selecionar dias da semana
- Ativar/desativar lembretes
- Editar e excluir lembretes
- Notificações locais no horário agendado

### 🎨 Visualizador 3D

- Renderização interativa de modelos dentários
- Suporte GLTF/GLB
- Controles de toque:
  - **Rotação**: Arrastar com 1 dedo
  - **Zoom**: Pinch gesture
  - **Pan**: Arrastar com 2 dedos
- Performance otimizada com Filament
- Thread GL dedicada para rendering

---

## 🔒 Privacidade & Segurança

- ✅ **Dados locais**: Todas as informações são armazenadas localmente no dispositivo
- ✅ **Sem internet**: App funciona 100% offline
- ✅ **Sem tracking**: Nenhum dado do usuário é coletado ou enviado
- ✅ **Sem anúncios**: Experiência limpa e focada na educação
- ✅ **Open Source**: Código transparente e auditável

---

## 🧪 Testes

### Executar Testes Unitários

```bash
./gradlew test
```

### Executar Testes Instrumentados

```bash
./gradlew connectedAndroidTest
```

### Cobertura de Código

```bash
./gradlew jacocoTestReport
```

---

## 🐛 Problemas Conhecidos

- [ ] Modo escuro em desenvolvimento
- [ ] Suporte para tablets precisa de otimização
- [ ] Alguns modelos 3D complexos podem ter performance reduzida em dispositivos antigos

### Reportar Bugs

Encontrou um bug? [Abra uma issue](https://github.com/duartegauss/smilelab/issues) com:
- Descrição detalhada do problema
- Passos para reproduzir
- Versão do Android e dispositivo
- Screenshots (se aplicável)

---

## 🗺️ Roadmap

### v1.1.0 (Em breve)
- [ ] Modo escuro completo
- [ ] Mais modelos 3D (aparelho ortodôntico, implantes)
- [ ] Animações de escovação em 3D
- [ ] Estatísticas de progresso
- [ ] Conquistas e gamificação

### v1.2.0 (Futuro)
- [ ] Suporte multilíngue (EN, ES, FR)
- [ ] Widget para tela inicial
- [ ] Exportar/importar dados
- [ ] Integração com Google Fit
- [ ] AR (Realidade Aumentada) para visualização dentária

### v2.0.0 (Longo prazo)
- [ ] Modo multiplataforma (iOS com KMP)
- [ ] Sincronização em nuvem (opcional)
- [ ] Consulta virtual com dentistas
- [ ] Reconhecimento de imagem (foto dos dentes)

---

## 🤝 Contribuir

Contribuições são bem-vindas! Se você deseja contribuir:

1. **Fork** o repositório
2. Crie uma **branch** para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um **Pull Request**

### Diretrizes

- Siga o estilo de código Kotlin oficial
- Escreva testes para novas funcionalidades
- Atualize a documentação conforme necessário
- Mantenha os commits descritivos e organizados

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

```
MIT License

Copyright (c) 2026 Duarte Gauss

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 👨‍💻 Desenvolvedor

<div align="center">

### **Duarte Gauss**

[![Email](https://img.shields.io/badge/Email-joaquimmateus0404@gmail.com-red?style=for-the-badge&logo=gmail)](mailto:joaquimmateus0404@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-duartegauss-black?style=for-the-badge&logo=github)](https://github.com/duartegauss)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/duartegauss)

</div>

---

## 🙏 Agradecimentos

- **Google Filament Team** - Engine de renderização 3D incrível
- **Android Jetpack Team** - Ferramentas modernas para desenvolvimento Android
- **Material Design Team** - Sistema de design consistente e bonito
- **Comunidade Open Source** - Bibliotecas e recursos valiosos

---

## 📞 Suporte

Precisa de ajuda? Entre em contato:

- 📧 Email: [joaquimmateus0404@gmail.com](mailto:joaquimmateus0404@gmail.com)
- 🐛 Issues: [GitHub Issues](https://github.com/duartegauss/smilelab/issues)
- 💬 Discussões: [GitHub Discussions](https://github.com/duartegauss/smilelab/discussions)

---

## ⭐ Mostre seu Apoio

Se este projeto foi útil para você, considere dar uma ⭐ no GitHub!

[![Star History](https://img.shields.io/github/stars/duartegauss/smilelab?style=social)](https://github.com/duartegauss/smilelab/stargazers)

---

<div align="center">

**Desenvolvido com ❤️ e Kotlin**

**SmileLab** © 2026 | Educação em Saúde Bucal para Todos

</div>

