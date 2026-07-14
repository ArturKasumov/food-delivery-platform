# psp-simulator-service

A local payment provider simulator for testing hosted checkout and callback flows.

## Run

```powershell
mvn spring-boot:run
```

Default URL:

```text
http://localhost:8084/api/v1/psp-simulator
```

## Create Checkout Session

```http
POST /api/v1/psp-simulator/checkout/session
Content-Type: application/json
```

```json
{
  "paymentId": "11111111-1111-1111-1111-111111111111",
  "orderId": "22222222-2222-2222-2222-222222222222",
  "amount": 250.00,
  "currency": "UAH",
  "callbackUrl": "http://localhost:8083/api/v1/payments/callback/psp-simulator"
}
```

The response contains a `checkoutUrl`. Open it in the browser and choose Pay or Cancel.
The simulator creates at most one checkout session for each `paymentId` and sends one HMAC-signed callback attempt to the supplied callback URL.
