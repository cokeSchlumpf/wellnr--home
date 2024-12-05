# Home Automation

## Weather API Requests

The application uses [Bright Sky](https://brightsky.dev/docs/#/operations/getCurrentWeather) Weather API to retrieve current weather situation and determine current lightning conditions.

```bash
curl --request GET \
  --url "https://api.brightsky.dev/current_weather?lat=49.442009&lon=6.636030" \
  --header "Accept: application/json"
```

Sample Response

```json
{
  "weather": {
    "source_id": 9779,
    "timestamp": "2024-12-05T18:30:00+00:00",
    "cloud_cover": 100,
    "condition": "fog",
    "dew_point": 1.96,
    "solar_10": 0.0,
    "solar_30": 0.0,
    "solar_60": 0.0,
    "precipitation_10": 0.0,
    "precipitation_30": 0.0,
    "precipitation_60": 0.0,
    "pressure_msl": 1016.4,
    "relative_humidity": 99,
    "visibility": 571,
    "wind_direction_10": 220,
    "wind_direction_30": 220,
    "wind_direction_60": 220,
    "wind_speed_10": 25.9,
    "wind_speed_30": 26.3,
    "wind_speed_60": 27.0,
    "wind_gust_direction_10": 210,
    "wind_gust_direction_30": 210,
    "wind_gust_direction_60": 220,
    "wind_gust_speed_10": 33.8,
    "wind_gust_speed_30": 34.9,
    "wind_gust_speed_60": 40.3,
    "sunshine_30": 0.0,
    "sunshine_60": 0.0,
    "temperature": 2.1,
    "fallback_source_ids": {
      "cloud_cover": 246081,
      "condition": 246081,
      "dew_point": 246081,
      "solar_10": 246081,
      "solar_30": 246081,
      "solar_60": 246081,
      "pressure_msl": 246081,
      "relative_humidity": 246081,
      "visibility": 246081,
      "wind_direction_10": 246081,
      "wind_direction_30": 246081,
      "wind_direction_60": 246081,
      "wind_speed_10": 246081,
      "wind_speed_30": 246081,
      "wind_speed_60": 246081,
      "wind_gust_direction_10": 246081,
      "wind_gust_direction_30": 246081,
      "wind_gust_direction_60": 246081,
      "wind_gust_speed_10": 246081,
      "wind_gust_speed_30": 246081,
      "wind_gust_speed_60": 246081,
      "sunshine_30": 246081,
      "sunshine_60": 246081,
      "temperature": 246081
    },
    "icon": "fog"
  },
  "sources": [
    {
      "id": 9779,
      "dwd_station_id": "03263",
      "observation_type": "synop",
      "lat": 49.4566,
      "lon": 6.6266,
      "height": 171.0,
      "station_name": "Merzig",
      "wmo_station_id": "J709",
      "first_record": "2024-12-04T12:30:00+00:00",
      "last_record": "2024-12-05T18:30:00+00:00",
      "distance": 1762.0
    },
    {
      "id": 246081,
      "dwd_station_id": "00460",
      "observation_type": "synop",
      "lat": 49.2641,
      "lon": 6.68678,
      "height": 362.3,
      "station_name": "Berus",
      "wmo_station_id": "10704",
      "first_record": "2024-12-04T12:30:00+00:00",
      "last_record": "2024-12-05T18:30:00+00:00",
      "distance": 20144.0
    }
  ]
}
```