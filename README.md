# CycleOurCity
CycleOurCity

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
| --- |

| Activate `{server_url}/users/activate?token=<token>` |
| --- |
| This method enables a user to activate his account, assuming that the provided token is correct. Only when a user has activated his account will he be able to perform security sensitive operations |
| --- |

| Reset `{server_url}/users/reset` |
| --- |
| `@Secured` |
| --- |
| Not implemented yet |

| Change (through interface) `{server_url}/users/change` |
| --- |
| `@Secured` |
| --- |
| Not implemented yet |

| Change (through token) `{server_url}/users/change` |
| --- |
| `@Secured` |
| --- |
| Not implemented yet |

#### AuthenticationResource

This service was designed to enable users to perform authentication-based requests.
In particular, this service enables the users to login and logout.

| Login `{server_url}/auth/login?user=<username/email>&password=<password>` |
| --- |
| This method allows a client to authenticate himself. In order to do so he must provide is username/email and password as url encoded values. If successfull, the method return a `JWT Token` that is mandatory for all security sensitive requests. |
| --- |

| Logout `{server_url}/auth/logout` |
| --- |
| This method allows a client to log himself out. In order to do so the request's header must contain the token, which is invalidated. |
| --- |

#### PlannerResouce

This service was designed to enable users to query for the best route to get from
point A to point B. 

| Plan Route `{server_url}/route` |
| --- |
| This is a unsecure method that enables any user to search for a route. For this to be possible the request's body must contain a JSON encoded payload that specifies *from latitude*, *from longitude*, *to latitude*, *to longitude*, *safety preference*, *elevation preference* and *time preference*, where the sum of the preferences must be equal to one. |
| --- |
| *Example :* `{ fromLat:<double>, fromLon:<double>, toLat:<double>, toLon:<double>, safetyPref:<float>, elevationPref:<float> timePref:<float> }` |
| --- |

