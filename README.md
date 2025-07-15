# LightHouseCourse

## Configuration Setup

This application requires external service credentials for OAuth authentication. Follow these steps to set up your configuration:

1. Copy the sample configuration file:
   ```
   src/main/resources/config.sample.properties
   ```
   
   to a new file:
   ```
   src/main/resources/config.properties
   ```

2. Edit `config.properties` and add your actual credentials:
   ```
   google.client.id=YOUR_GOOGLE_CLIENT_ID
   google.client.secret=YOUR_GOOGLE_CLIENT_SECRET
   ...
   facebook.client.id=YOUR_FACEBOOK_APP_ID
   facebook.client.secret=YOUR_FACEBOOK_APP_SECRET
   ...
   ```

3. Note: The `config.properties` file contains sensitive information and is excluded from version control in `.gitignore`.

## OAuth Configuration

To set up OAuth for this application:

### Google OAuth
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project or select an existing one
3. Enable the Google+ API
4. Create OAuth 2.0 credentials and configure the redirect URI as:
   ```
   http://localhost:8080/LightHouseCourse/login?method=google
   ```

### Facebook OAuth
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create an app or select an existing one
3. Add the Facebook Login product
4. Configure the Valid OAuth Redirect URI as:
   ```
   http://localhost:8080/LightHouseCourse/login?method=facebook
   ```

## Running the Application

[Include instructions for running your application here] 