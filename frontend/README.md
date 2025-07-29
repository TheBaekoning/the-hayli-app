# Hayli Health Frontend

Built with:
* [Svelte](https://svelte.dev/)
* [SvelteKit](https://kit.svelte.dev/)
* [Capacitor](https://capacitorjs.com/)

## Requirements

### Service Workers
To work on anything to do with service workers, you will need to work over HTTPS. You *can* do this via self-signed certificates, but it's a bit of a pain. Instead, you can use a tool like [zrok](https://zrok.io/) or [ngrok](https://ngrok.com) to create a secure tunnel to your local server.

### iOS
Debugging iOS devices will require
* Mac
* iPhone/iPad
* XCode
* XCode Command Line
* Homebrew
* CocoaPods
Instructions for setting up the environment can be found on [Capacitor's Website](https://capacitorjs.com/docs/getting-started/environment-setup#ios-requirements);

### Android
Debugging Android devices have less requirements
* Android Studio
* Android SDK 28 (Android 9.0)
	* Building down to Android 9.0 achieves [>90% coverage as of 2024-05-16](https://gs.statcounter.com/android-version-market-share)
	* Android 9 released in 2018


## Developing

Once you've created a project and installed dependencies with `npm install` (or `pnpm install` or `yarn`), start a development server:

```bash
npm run dev

# or start the server and open the app in a new browser tab
npm run dev -- --open
```

## Building

### Node Server

1. Build the app
	```bash
	pnpm run build
	```
2. SFTP to `app.haylihealth.com` server and upload the `build` directory into the user's home directory, overwriting the existing `build` directory.
3. SSH into the server and navigate to the `build` directory and install the dependencies
	```bash
	pn i -P
	```
4. Restart PM2
	```bash
	pm2 restart all
	```

### Apps

1. First time setup. Skip this step if you've already done this in this folder (you have `android` or `ios` folders)
	```bash
	npx cap add android
	npx cap add ios
	```
2. Build the app
	```bash
	npm run build:spa
	```
3. Sync the app with the native projects
	```bash
	npx cap sync
	```
4. Open the native project in the respective IDEs
	```bash
	npx cap open android
	npx cap open ios
	```
