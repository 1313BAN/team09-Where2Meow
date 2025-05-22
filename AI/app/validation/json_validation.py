import json
from jsonschema import validate, ValidationError

# 관광지 데이터 JSON 스키마 정의
schema = {
    "type": "object",
    "properties": {
        "attractions": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "uniqueKey": {"type": "string"},
                    "attractionId": {"type": "integer"},
                    "attractionName": {"type": "string"},
                    "addr": {"type": "string"},
                    "categoryId": {"type": "integer"},
                    "categoryName": {"type": "string"},
                    "date": {"type": "string"},
                    "attractionOrder": {"type": "integer"}
                },
                "required": [
                    "uniqueKey", "attractionId", "attractionName", "addr",
                    "categoryId", "categoryName", "date", "attractionOrder"
                ]
            }
        }
    },
    "required": ["attractions"]
}

def validate_json(data):
    try:
        json_data = json.loads(data)
        validate(instance=json_data, schema=schema)
        return True
    except (json.JSONDecodeError, ValidationError) as e:
        return False
