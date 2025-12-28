<a name="readme-top"></a>

<!-- Presentation Block -->
<br />

<div align="center">

  <a href="https://github.com/LightDestory/FilamentRFIDTool">
    <img src="https://raw.githubusercontent.com/LightDestory/FilamentRFIDTool/master/.github/assets/images/presentation_image.png" alt="Preview" width="90%">
  </a>

  <h2 align="center">FilamentRFIDTool</h2>
  
  <p align="center">
      An Android application to manage Filament RFIDs.
  </p>
  
  <br />
  <br />

</div>

<!-- ToC -->

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#book-about-the-project">📖 About The Project</a>
    </li>
    <li>
      <a href="#gear-getting-started">⚙️ Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#usage">Usage</a></li>
        <li><a href="#notes">Notes</a></li>
      </ul>
    </li>
    <li><a href="#dizzy-contributing">💫 Contributing</a></li>
    <li><a href="#handshake-support">🤝 Support</a></li>
    <li><a href="#warning-license">⚠️ License</a></li>
    <li><a href="#hammer_and_wrench-built-with">🛠️ Built With</a></li>
  </ol>
</details>

<!-- About Block -->

## :book: About The Project

FilamentRFIDTool is an Android application designed to read and manage RFID tags on 3D printing filament spools. It features a modern UI and easy-to-use interaction, allowing users to read, write, and store RFID data.

Currently, there are three main types of filament tags in the wild: **Bambu Lab**, **Creality**, and **OpenTag**. This application aims to support all of them.

For more technical details on the tags, refer to the [Bambu Research Group RFID Tag Guide](https://github.com/Bambu-Research-Group/RFID-Tag-Guide#tag-documentation).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Setup Block -->

## :gear: Getting Started

### For Users

If you just want to use the application, you can download the latest APK from the [Releases](https://github.com/LightDestory/FilamentRFIDTool/releases) section.

### For Developers

To get a local copy up and running for development, follow these simple steps.

#### Prerequisites

* Android Studio Ladybug or newer
* JDK 17
* Android Device with NFC support

#### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/LightDestory/FilamentRFIDTool.git
   ```
2. Open the project in Android Studio
3. Sync Gradle project
4. Run on your device

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Usage

The application is designed with simplicity in mind, featuring three main views:

*   **Scanner**: Quickly scan an RFID tag on the fly to check its data.
*   **Vault**: Store and manage your collection of RFID data.
*   **About**: View application information.

Interaction is easy—simply use the scan button to read an RFID tag.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Notes

This project is currently in development.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Contribute Block -->

## :dizzy: Contributing

If you are interested in contributing, please refer to [Contributing Guidelines](.github/CONTRIBUTING.md) for more information and take a look at open issues. Ask any questions you may have and you will be provided guidance on how to get started.

Thank you for considering contributing.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Support Block -->

## :handshake: Support

If you find value in my work, please consider making a donation to help me create, and improve my projects.

Your donation will go a long way in helping me continue to create free software that can benefit people around the world.

<p align="center">
<a href='https://ko-fi.com/M4M6KC01A' target='_blank'><img src='https://raw.githubusercontent.com/LightDestory/FilamentRFIDTool/master/.github/assets/images/support.png' alt='Buy Me a Hot Chocolate at ko-fi.com' width="45%" /></a>
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- License Block -->

## :warning: License

The content of this repository is distributed under the GNU GPL-3.0 License. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Built With Block -->

## :hammer_and_wrench: Built With

- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material3 Compose](https://developer.android.com/jetpack/compose/designsystems/material3)
- [AndroidX Navigation](https://developer.android.com/guide/navigation)
- [Bouncy Castle Provider](https://www.bouncycastle.org/)
- [Android Studio](https://developer.android.com/studio)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
