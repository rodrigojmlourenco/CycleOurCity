# CycleOurCity
CycleOurCity

* * * 

## Software Requirements

| Software Requirements |
| --- |
| Maven (v3) |
| Java 1.8 |
| MySQL (preferably MariaDB) |
| Python |

| Maven Dependencies (Not found in a maven remore repository)|
| --- |
| GeoTools ([Maven Instalation Guide](http://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html)) |
| OpenTripPlanner - [CycleOurCity Fork](https://github.com/rodrigojmlourenco/OpenTripPlanner) |

* * *

## Running the Project for the First Time

### Scripts

To ease the deployment of a CycleOurCity server, several scripts were designed.
In this section, we describe these scripts and their purpose.

The scripts assume the existance of a `config.xml`, located in the same directory as the scripts.
This configuration file specifies several properties, namely:

```XML
<config>
			<!-- MySQL/MariaDB properties -->
        <repository>
          <user val='user'/>
          <password val='password'/>
        </repository>   
			<!-- Map view box -->
        <map>
          <bottom-left lat="38.7260" lon="-9.1620"/>
          <top-right lat="38.7553" lon="-9.1249"/>
        </map>
</config>
```
| setup-db.py |
| --- |
| This script sets up the database schema, which is employed in CycleOurCity. |

| setup-osm-data.py |
| --- |
| Given the defined viewbox values, this script fetches an osm data file. This file is then used by CycleOurCity to generate a graph, and populate the streetedges knowledge of the system. The file is stored in an `otp` folder, located at the `$HOME$` environment variable. |

| setup-secret.py |
| --- |
| Generates a secret that is employed by the server's security manager to generate session JWT tokens. |

> __IMPORTANT__ Before the CycleOurCity server is deployed, these two scripts must first be executed.

### Instalation and Execution Step-by-Step

1. In the folder `driver` run the following command: `mvn install`
2. In the folder `coc-otp` run the following command: `mvn install`
3. In the folder `rest-server` run the following command: `mvn clean package exec:java -DskipTests`


* * * 

## Modules

### driver

### coc-otp

### rest-server

The `rest-server` project is the actual server per se. This is a JAX-RS (Jersey2)
project that makes available several services. Each service was designed to
support some type of functionality. In particular the `rest-server` offers the
following services:

* AuthenticationResouce `{server_url}/auth`

* PlannerResource `{server_url}/route`

* RateStreetsResource `{server_url}/streets`

* TripResouce `{server_url}/trip`

* UsersResource `{server_url}/users`

| *Note* | The `@Secured` will denote any security sensitive operations | 

### UsersResouce 

This service was designed to enable users to manage their account. Therefore, it
supports the creation, updation and deactivation of user accounts.

| Register `{server_url}/users/register` | 
| --- |
| This method enables a new user to be registered. The fields must be provided as part as the request body, ideally in as a JSON encoded string. This payload must contain *username*, *email*, *password* and *confirmation password*. `TODO: specify the properties of each field.` If successfull, the method will return a token that enables the user's account to be activated. `TODO: the token should be sent by email`. |

| Activate `{server_url}/users/activate?token=<token>` |
| --- |
| This method enables a user to activate his account, assuming that the provided token is correct. Only when a user has activated his account will he be able to perform security sensitive operations |

| Reset `{server_url}/users/reset` |
| --- |
| `@Secured` |
| Not implemented yet |

| Change (through interface) `{server_url}/users/change` |
| --- |
| `@Secured` |
| Not implemented yet |

| Change (through token) `{server_url}/users/change` |
| --- |
| `@Secured` |
| Not implemented yet |

#### AuthenticationResource

This service was designed to enable users to perform authentication-based requests.
In particular, this service enables the users to login and logout.

| Login `{server_url}/auth/login?user=<username/email>&password=<password>` |
| --- |
| This method allows a client to authenticate himself. In order to do so he must provide is username/email and password as url encoded values. If successfull, the method return a `JWT Token` that is mandatory for all security sensitive requests. |

| Logout `{server_url}/auth/logout` |
| --- |
| This method allows a client to log himself out. In order to do so the request's header must contain the token, which is invalidated. |

#### PlannerResouce

This service was designed to enable users to query for the best route to get from
point A to point B. 

| Plan Route `{server_url}/route` |
| --- |
| This is a unsecure method that enables any user to search for a route. For this to be possible the request's body must contain a JSON encoded payload that specifies *from latitude*, *from longitude*, *to latitude*, *to longitude*, *safety preference*, *elevation preference* and *time preference*, where the sum of the preferences must be equal to one. |
| *Payload :* `{ fromLat:<double>, fromLon:<double>, toLat:<double>, toLon:<double>, safetyPref:<float>, elevationPref:<float> timePref:<float> }` |

| Plan Route `{server_url}/route` |
| --- |
| `@Secured` |
| Identical to the previous one, however, the planned route is stored as a trip. In order for a user to classify a street, that street must be part of one of his trips. That is, before an authenticated user may contribute by classifying a street, he must first request a route plan that contains that street. |

#### RatedStreetsResouce

This service enables the users to contribute by classifying the criteria that characterize differente street edges.
Additionally, it also provides some unsecure method to fetch geometries. * This methods were implemented as they were part of the original architecture, however, their purpose is not clear. Therefore, and at this stage, this methods will not be described and detailed.*

| Rate Trip `{server_url/streets}` |
| --- |
| `@Secured` |
| This method enables users to classify the streets that belong to a certain trip. In order to do so, the request's body must contain a JSON encoded payload (described bellow). If successfull, the user provided criterion will be added to the backend database, and ultimately lead to better route recommendations. Finally, the user's trip will be removed (*This choice was adopted as this was performed in the original architecture*). |
| *Payload :* `{tripId:<int>, ratings:<JSONArray[{streetEdgeId:<string>, elevationRate:<int>, safetyRate:<int>, pavementRate:<int>, railsRate:<int>}]}` |
