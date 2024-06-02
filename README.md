# Text Expander App - `espanso` app companion (not official)

Text Expander is an Android application that allows users to expand text shortcuts into longer predefined phrases. It uses an Accessibility Service to monitor text input and replace triggers with corresponding values in any application.
The app allows you to select a folder in your Documents folder, and load all the yaml files in the match folder insider the selected folder.

You can use it in 2 ways:

1. in the app, you can list, search and copy to clipboard all your triggers and replacements inside all your yaml files. 
2. you can activate the accessibility service and use text expansion in all the apps on your phone (as far as I have checked). You can also activate the Text Expander shortcut to get a small button to activate and deactivate the text expansion on typing feature.


![TextExpander Main Screen](docs/imgs/TextExpanderApp-MainScreen.png)
![TextExpander Accessibility Shortcut](docs/imgs/TextExpanderAPP-Shortcut.png)

# Why?

I am now using Linux and Windows at work, and just got an Android Phone and I thought it would be cool to get access to my `espanso` triggers on it. It works just with simple replacements, but I think it is enough for now.  

# Disclaimer

- This is my very first Android App. I made heavy use fo ChatGpt 4. 4o, Perplexity, Google and Coffee ☕.
- I expect there to be errors and, should you find any, you are welcome to create an Issue and fix it. 😁
- The app is usable as it is now, with an ugly logo and user interface, on at least Android 13 and Android 14.

## Features

- **Parse YAML Files:** Reads `.yml` files from the `Documents/espanso/match` directory to load text expansion triggers and values.
- **List Files and Triggers:** Displays the list of YAML files and their triggers and values.
- **Search Functionality:** Provides a search bar to filter triggers and values.
- **Clipboard Copy:** Copies the value of a trigger to the clipboard when a trigger-value pair is clicked.
- **Text Expansion:** Expands text shortcuts into longer predefined phrases in any app using an Accessibility Service.

## Setup and Usage

### Prerequisites

- Android Studio installed on your computer.
- A device or emulator running Android 13 or higher.

### Installation

1. Clone the repository:
   ```shell
   git clone https://github.com/dacog/textexpander_android.git
   ```
2. Open the project in Android Studio.
3. Build and run the project on your device or emulator.

**Note:**

**Direct APK Installation**:
   - **Recommended Method**: Use ADB or Android Studio's Device Manager to avoid security restrictions.
   - **Alternative Method**: If you need to install the APK directly, such as through a file transfer:
      - Transfer the APK file to your device.
      - Ensure you have enabled **Install from Unknown Sources** in your device settings.
      - Use a file manager to locate and install the APK.
      - Note: Direct installation methods may encounter additional security prompts or restrictions.
     - To enable the accessibility service for an app installed from the file system, you typically need to follow these steps:
       - Go to Settings > Accessibility > Installed Apps. 
       - Select the app you want to grant accessibility services to (it will be grayed out initially). 
       - Go back to the main Settings menu, then navigate to Apps > TextExpander. 
       - Tap the three-dot menu in the top-right corner and select "Allow restricted settings."
       - After granting the necessary permission, go back to the Accessibility menu and enable the accessibility service for your app.


### Configuration

1. Place your YAML files in the `Documents/espanso/match` directory on your device. The YAML files should contain triggers and replacement values in the following format:

   ```yaml
   matches:
     - trigger: ":example"
       replace: "This is an example replacement text."
   ```
   
You can use an app such as [FolderSync](https://foldersync.io/) to sync your `espanso` folder with the phone from Dropbox, Google Drive, OneDrive or WebDav (NextCloud).

You may also use git or just copy the `espanso` folder you have in your computer to your Documents folder on your phone.

2. Open the Text Expander app and select the `Documents/espanso` folder where the `match` directory is in. The app will parse the YAML files and display the triggers and values.

### Enabling the Accessibility Service

1. Go to `Settings` -> `Accessibility`.
2. Find and select `Text Expander Accessibility Service`.
3. Enable the service.

**You can check the code, but the app just uses the accessibility service to expand your triggers. Nothing more, nothing less.** 


### Using the App

- **Search Triggers:** Use the search bar to filter triggers and values.
- **Copy to Clipboard:** Tap on a trigger-value pair to copy the value to the clipboard.
- **Text Expansion:** Type a trigger in any app and see it expand to the predefined value.

## Code Structure

- `MainActivity.kt`: Handles the UI, file selection, parsing YAML files, and displaying the list of triggers and values.
- `TextExpanderService.kt`: Monitors text input and performs text expansion using the triggers loaded from YAML files.
- `CustomTriggerAdapter.kt`: A custom adapter for the `ListView` to implement fuzzy search functionality.

## Dependencies

- [SnakeYAML](https://bitbucket.org/asomov/snakeyaml): A YAML parser for Java.
- AndroidX Libraries: Core, AppCompat, Material, Activity KTX, and DocumentFile.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes. 

As I wrote in the Disclaimer above, I expect there to be errors and you are welcome to create an Issue and fix any error you may find 😁

# TODO

- [ ] Testing in other devices. I tested on emulators with android 13 and 14 and on my Moto Edge 20 running Android 14.
- [ ] A better logo.
- [ ] add tests
- [ ] add workflow to build the app
- [ ] add the app to playstore and fdroid

## License

This project is licensed under the GPL v3 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- This project uses the [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml/) library for parsing YAML files.
- [Espanso](https://github.com/espanso/espanso) from [Federico Terzi](https://federicoterzi.com/). This is why I though of making the app. 

## Contact

You can ifnd me in [LinkedIn](https://www.linkedin.com/in/diegocarrasco/), in [GitHub](https://github.com/dacog/) and on [my page](https://diegocarrasco.com/)