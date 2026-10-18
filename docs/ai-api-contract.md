# AI API integration contract

The Spring Boot backend owns authentication, validation, persistence, and batch processing. The future ML service only needs one endpoint:

`POST {AI_API_URL}/predict`

If `AI_API_KEY` is set, the backend sends it as `Authorization: Bearer <key>`. The JSON request uses the same camelCase fields as `POST /api/predictions`, including all 24 clinical features. A successful response must be:

```json
{
  "outcome": "CKD_RISK",
  "riskScore": 0.88,
  "modelVersion": "random-forest-2026-01"
}
```

- `outcome`: `CKD_RISK` or `NORMAL`
- `riskScore`: number from `0` to `1`
- `modelVersion`: deployed model identifier

SHAP plots and CKD stage estimation are deliberately outside this contract, as requested.
