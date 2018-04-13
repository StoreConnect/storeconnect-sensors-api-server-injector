# FUI StoreConnect Sensor API's server injector

Simple utility tool to inject data to a [FUI StoreConnect Sensors API server](https://github.com/StoreConnect/storeconnect-sensors-api-server)

## Prerequisites

The [FUI StoreConnect Sensors API Client](https://github.com/StoreConnect/storeconnect-sensors-api-client) needs to be installed.

## Example of use

```bash
$ mvn clean package
$ java -jar target/storeconnect-sensors-api-server-injector-<VERSION>-jar-with-dependencies.jar \
    --enpoint http://storeconnect-sensors-api-server/SensorThingsService/1.0 \
    --input file:///path/to/the/data/file.json \
    --type flat-motion
```

## Available data types

The `-t` or `--type` is used to precise the type of the data to inject to the StoreConnect Sensor API's server.

Hereafter the list of available data types

### `flat-motion`

When using the `flat-motion` data type, input file has to follow the StoreConnect's flat motion observation format.

An example of the StoreConnect's flat motion observation format is given bellow:

```json
[
    {
        "appuserid": "Cam1_T3_6",
        "building": 1,
        "devicedate": 1512042846315,
        "floor": 0,
        "lat": 50.633479927598877,
        "lon": 3.0241943673786045,
        "type": "location",
        "venueid": "95"
    },
    {
        "appuserid": "Cam1_T3_6",
        "building": 1,
        "devicedate": 1512042846460,
        "floor": 0,
        "lat": 50.63347967549938,
        "lon": 3.0241948411172297,
        "type": "location",
        "venueid": "95"
    },
    {
        "appuserid": "Cam1_T3_6",
        "building": 1,
        "devicedate": 1512042846605,
        "floor": 0,
        "lat": 50.633478499035071,
        "lon": 3.0241970518974806,
        "type": "location",
        "venueid": "95"
    }
]
```
## How to contribute

Feel free to contribute by making a `pull request` following the [contributing](./CONTRIBUTING.md) instructions.

## License

Copyright 2018 Inria Lille

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.