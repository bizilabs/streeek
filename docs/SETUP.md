# Welcome To Streeek Development

This document provides a detailed guide on how to set up and run the project.
Follow the steps below to ensure a smooth setup process.

In order to setup the project properly we'll need a github app and supabase account

# Table Of Contents
- [Fork & Clone](#fork--clone-) 
  - [Fork Repository](#fork-the-repository) 
  - [Clone Repository](#clone-the-repository) 
- [Create Github App](#create-github-app) 
  - [Navigate to OAuth apps](#navigating) 
  - [Create an OAuth apps](#creating) 

## Fork & Clone 
### Fork the Repository

- Navigate to the GitHub repository page.

- Click the Fork button in the top-right section to create a copy of the repository under your GitHub account.

| Fork                                                            | 
|-----------------------------------------------------------------|
| <img src="../.github/images/setup/github/fork.png" width="300"> |


### Clone The Repository

- Copy the URL of your forked repository e.g. `https://github.com/<your-username>/<repository-name>.git`.
  - while forking the repository you can change the repository name if you wish :wink:.

- Open a terminal or command prompt and run the following command:

    ```shell
    git clone https://github.com/<your-username>/<repository-name>.git
    ```

- Navigate into the cloned repository directory:
    ```shell
    cd <repository-name>
    ```
- Inside the repository create a `local.properties` file if it's not created.

## Create Github App

Create a github app

### Navigating
- Click on your `profile` image on the top right

| Click Profile                                                            | 
|--------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/click_profile.png" width="300"> |

- In the pop up sheet that appears click on `settings`

| Click Settings                                                            | 
|---------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/click_settings.png" width="300"> |

- In your settings screen click on `developer settings` at the bottom left

| Click Settings                                                                      | 
|-------------------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/click_developer_settings.png" width="300"> |

- In your `developer settings` screen click on `OAuth Apps` & then click on `New OAuth app` action

| Click Settings                                                              | 
|-----------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/click_oauth_apps.png" width="300"> |

### Creating
- Fill the `Register a new OAuth app` with app name, homepage url and description of your choosing and set callback url to <font color="red">`streeek://app.mobile`</font> like in the example below.
> :warning:  **Important:**  Ensure the callback url is : `streeek://app.mobile`.

| Register Application                                                            | 
|---------------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/register_application.png" width="300"> |

- Click on `Register application`
- You'll be presented with your application page where you can update the details and view your `Client ID`

| Application                                                            | 
|------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/application.png" width="300"> |

- click on `Generate a new client secret` and ensure you save the secret before leaving the page.

| Secrets                                                                  | 
|--------------------------------------------------------------------------|
| <img src="../.github/images/setup/github/client_secret.png" width="300"> |

- Now that you have created your github app, save the following details in your `local.properties` folder 

    ```properties
    # Declare Github Secrets
    github.client.id=your-github-app-client-id
    github.client.name=your-github-app-name
    github.client.secret=your-github-app-client-secret
    github.client.redirect.url=streeek://app.mobile
    ```
  
