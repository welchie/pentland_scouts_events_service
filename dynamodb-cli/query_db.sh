aws dynamodb query --table-name=PersonData --key-condition-expression "uid = :uid" --expression-attribute-values '{":uid":{"S":"123456"}}' --endpoint-url=http://localhost:8000
