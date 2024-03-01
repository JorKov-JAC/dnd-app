# Dungeons & Dragons Compendium
A group project created with [MakenaH](https://github.com/MakenaH) which stores information related to Dungeons & Dragons.

## Description
This app catalogues the wealth of information found in the world of Dungeons & Dragons. Users can create entries for monster stats, magical item descriptions, and miscellanea of similar ilk. Monsters are shared in a collective database and support custom images, while items are unique to each user.

## What I did
I handled the "Monsters" part of the project, as well as implementing the search system and various other tasks. Files all have comments at their top stating who programmed what.

## Quick-start
- Download the APK and install it on your Android phone or emulator.

Or:
- Clone the repository: `git clone https://github.com/JorKov-JAC/dnd-app`
- Create a new [Firebase](https://console.firebase.google.com) project. Within the Firebase console:
	- Setup "Firestore Database" and "Storage"
		- Use the following security rules: `allow read, write: if true;`
	- Setup "Authentication" using Email/Password
	- Create a "google-services.json":
		- Android package name: makovacs.dnd
		- Add the file to the "/app" directory as instructed.
- Install [Android Studio](https://developer.android.com/studio).
- Open the repository in Android Studio.
- Run the app in Android Studio by clicking **Run > Run 'app'** or pressing **Shift+F10**.

## Screenshots of application
<div align="center">
<h3>About screen</h3>
<figure>
<figcaption>A screen giving some information about the project and creators.</figcaption>
<br/>
<img src="./assets/about.png"/>
</figure>

<h3>Contact screen</h3>
<figure>
<figcaption>A screen giving some information about how the potential company could be contacted.</figcaption>
<br/>
<img src="./assets/contact.png"/>
</figure>

<h3>Account screens</h3>
<figure>
<figcaption>The screen to sign in to an account.</figcaption>
<br/>
<img src="./assets/accsignin.png"/>
</figure>

<br/>
<figure>
<figcaption>A screen allowing users to sign out and delete their account.</figcaption>
<br/>
<img src="./assets/accoverview.png"/>
</figure>

<h3>Monster screens</h3>
<figure>
<figcaption>A list view of all the created monsters.</figcaption>
<br/>
<img src="./assets/monscreen.png"/>
</figure>

<br/>
<figure>
<figcaption>A screen allowing the user to create a new monster.</figcaption>
<br/>
<img src="./assets/moninput.png"/>
</figure>

<br/>
<figure>
<figcaption>A screen showing the details of a monster.</figcaption>
<br/>
<img src="./assets/mondetail.png"/>
</figure>

<h3>Magic screens</h3>
<figure>
<figcaption>A list view of all the created magic items.</figcaption>
<br/>
<img src="./assets/magscreen.png"/>
</figure>

<br/>
<figure>
<figcaption>A screen allowing the user to create a new magic item.</figcaption>
<br/>
<img src="./assets/maginput.png"/>
</figure>

<br/>
<figure>
<figcaption>A screen showing the details of a magic item.</figcaption>
<br/>
<img src="./assets/magitem.png"/>
</figure>
</div>

## Team members
- Makena Howat (2139389@johnabbottcollege.net)
- Jordan Kovacs (2088248@johnabbottcollege.net)
