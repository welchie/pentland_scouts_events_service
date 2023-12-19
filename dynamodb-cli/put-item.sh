aws dynamodb put-item --table-name=PersonData --item file://person_data.json --endpoint-url=http://localhost:8000 --return-consumed-capacity TOTAL --return-item-collection-metrics SIZE
