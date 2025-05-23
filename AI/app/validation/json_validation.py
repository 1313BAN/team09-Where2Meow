import json
from jsonschema import validate, ValidationError

# 관광지 데이터 JSON 스키마 정의
create_schema = {
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
                    "attractionOrder": {"type": "integer"},
                    "content": {"type": "string"},
                },
                "required": [
                    "uniqueKey",
                    "attractionId",
                    "attractionName",
                    "addr",
                    "categoryId",
                    "categoryName",
                    "date",
                    "attractionOrder",
                ],
            },
        }
    },
    "required": ["attractions"],
}

update_schema = {
    "type": "object",
    "properties": {
        "attractions": {
            "type": "array",
            "items": {
                "oneOf": [
                    {
                        "type": "object",
                        "properties": {
                            "mode": {"const": "add"},
                            "uniqueKey": {"type": "string"},
                            "attractionId": {"type": "integer"},
                            "attractionName": {"type": "string"},
                            "addr": {"type": "string"},
                            "lat": {"type": "number"},
                            "lng": {"type": "number"},
                            "categoryId": {"type": "integer"},
                            "categoryName": {"type": "string"},
                            "date": {"type": "string"},
                            "attractionOrder": {"type": "number"},
                            "content": {"type": "string"},
                        },
                        "required": [
                            "mode",
                            "uniqueKey",
                            "attractionId",
                            "attractionName",
                            "addr",
                            "lat",
                            "lng",
                            "categoryId",
                            "categoryName",
                            "date",
                            "attractionOrder",
                            "content",
                        ],
                    },
                    {
                        "type": "object",
                        "properties": {
                            "mode": {"const": "delete"},
                            "uniqueKey": {"type": "string"},
                        },
                        "required": ["mode", "uniqueKey"],
                    },
                ]
            },
        }
    },
    "required": ["attractions"],
}


def validate_json(data, mode):
    try:
        json_data = json.loads(data)
        if mode == "create":
            schema = create_schema
        elif mode == "update":
            schema = update_schema
        else:
            raise ValueError("Invalid mode. Use 'create' or 'update'.")
        validate(instance=json_data, schema=schema)
        return True
    except (json.JSONDecodeError, ValidationError) as e:
        return False
