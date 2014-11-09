- APIs with URI Contracts
- Versioning
- Multiple Identifiers per Entity
- Pagination
- Search
- Functional Operations
- Security/Trust model

```

    https://api.example.com/library/customer/v1/*1..10+name/~UNIQUE/first_name
    |______________________||______||_______||_||__________||_______________|
                |              |       |      |      |          |
            environment        |     entity   |  criteria       |
                             service       version           aggregate

    https://api.example.com/library/customer/v1/@1234567890/order/v2.1/@012345
    |______________________||______||_______||_||__________||___||____||_____|
               |               |        |     |      |        |    |       |
            environment        |     entity   |  identifier   |   version  |
                            service          version      sub-entity    identifier (or criteria, etc, etc
```
** general
-  GETs on service, entity or version provides contract information
-  URIs from 'service' onwards (inlcuding any versions) provide the contract (so can be stored in code). A requirement to change the URI is likely to trigger a requirement to change the implementation also
-

*** environment
- effectively a baseURL
- protocol, host, (optional)port, (optional) path
- clients should always keep this in environment configuration

*** entity
- a top-level resource


*** environment
- effectively a baseURL
- protocol, host, (optional)port, (optional) path
- clients should always keep this in environment configuration

*** version
```
   v{major}.{minor}
```
- versions appear in the path section after the entity they are versioning (to make natural ordering of templates more useful)
- versions can be applied to any service, entity or sub-entity but are always optional (an implicit single version can exist)
- minor versions provide backwards-compatible modfications to contract and/or JSON schema
- major versions are breaking changes
- major and minor should both be integers
- 'v' on it's own is an acceptable first version
- 'vX' represents experimental. This will always track the latest version (so no backwards-compatibility contract but useful for early testing).

*** criteria
```
   *{from}..{to}+created_date
   |___________||___________|
         |            |
       range        sort
```
- criteria returns a list of entities
- ranges are 1 indexed not zero from and to are inclusive so '1..10' will return first 10 items)
- sort can be ascending (+) or descending (-)
- ascending is a->z, 0->999, 19700101T000001Z->19790303T091500Z

*** aggregate
- TODO
- aggregate operations can be run on criteria result sets (typically on the complete set '/*/')
- ~FUNCTION/{optional path to field}
- ~COUNT
- ~UNIQUE{path}, ~AVERAGE{path-to-number},  ~MIN{path-to-number},  ~MAX{path-to-number}


*** identifier
- @value specifies an individual resource by it's canonical identifier
- @{name}:value  allows the server to provide lookup on an alternative unique identifier. Redirects to the canonical
- MUST not be able to return more than one instance of an entity


** trust scope
- Instead of an identifier it is possible to use a trust scope
- !{trust scope} causes the access token to be resolved to a value which can be injected
- Allows a provider and authority to provide access to resources without exposing identifiers to the consumer (norr gateway pattern)



Notes:

```

            |   entity/version         |         *               |           @           |         ~         |
-----------------------------------------------------------------------------------------------------
GET         |   Contract               | List <EntitySummary>    |  1 Entity             |                   |
-----------------------------------------------------------------------------------------------------
GET         |   Contract               |                   |                       |                   |
-----------------------------------------------------------------------------------------------------
GET         |   Contract               |                   |                       |                   |
-----------------------------------------------------------------------------------------------------
GET         |   Contract               |                   |                       |                   |

List<Summary<
    Entity>>
1 Entity
Redirect to 1 Canonical Entity
Value (or List of)
?Entity?
POST
CREATE
SEARCH
UPDATE
COMPLEX
PUT
n/a
n/a
CREATE/UPDATE
n/a
PATCH
n/a
n/a
UPDATE
n/a
DELETE
n/a
FINISHED
DELETE
FINISHED
```
By default GETs are etagged and mutating calls modify the etag.