# Address API Spec

## Create Address

Endpoint : POST /api/contacts/{contactId}/addresses

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street" : "Street ...",
  "city" : "City ...",
  "province" : "Province ...",
  "country" : "Indonesia",
  "postalCode" : "12313"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "randomstring",
    "street" : "Street ...",
    "city" : "City ...",
    "province" : "Province ...",
    "country" : "Indonesia",
    "postalCode" : "12313"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Contact not found"
}
```

## Update Address

Endpoint : PUT /api/contacts/{contactId}/addresses/{addressId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street" : "Street ...",
  "city" : "City ...",
  "province" : "Province ...",
  "country" : "Indonesia",
  "postalCode" : "12313"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "randomstring",
    "street" : "Street ...",
    "city" : "City ...",
    "province" : "Province ...",
    "country" : "Indonesia",
    "postalCode" : "12313"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address not found"
}
```

## Get Address

Endpoint : GET /api/contacts/{contactId}/addresses/{addressId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "id" : "randomstring",
    "street" : "Street ...",
    "city" : "City ...",
    "province" : "Province ...",
    "country" : "Indonesia",
    "postalCode" : "12313"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address not found"
}
```

## Remove Address

Endpoint : DELETE /api/contacts/{contactId}/addresses/{addressId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address not found"
}
```

## List Address

Endpoint : GET /api/contacts/{contactId}/addresses

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data": [
    {
      "id": "randomstring",
      "street" : "Street ...",
      "city" : "City ...",
      "province" : "Province ...",
      "country" : "Indonesia",
      "postalCode" : "12313"
    }
  ]
}
```

Response Body (Failed) :

```json
{
  "errors" : "Contact not found"
}
```