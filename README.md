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

#### AuthenticationResource

This service was designed to enable users to perform authentication-based requests.
In particular, this service enables the users to login and logout.

| Login `{server_url}/auth/login?user=<username/email>&password=<password>` |
| --- |
| This method allows a client to authenticate himself. In order to do so he must
provide is username/email and password as url encoded values. If successfull, the 
method return a `JWT Token` that is mandatory for all security sensitive requests.
| --- |

