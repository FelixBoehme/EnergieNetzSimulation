# ⚡ Netzsimulation
A Spring Application for simulating an electricity grid consisting of energy stores.

Current features:
- add/remove stores
- fill up energy stores
- draw from network (2 strategies)
- get info on stores and network

## 📋 Requirements

- JDK 22
- Maven 4
- Keycloak

## 💻 Running locally
Start your Keycloak Server on port `8081` and create a new Realm using the `keycloakRealm.json` file.
This creates the new Realm _NetzSimulation_  with an _admin_ role.

Create a new user and assign them the `admin` role.

You can find the OpenAPI definition [here](http://localhost:8080/swagger-ui/index.html).
To test the application you can authenticate on the same page and make requests.
