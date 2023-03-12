# Shelly adapter
Control [Shelly](https://shelly.cloud/) devices via MQTT.

The adapter exposes relays as light entities or switch entities.

## Configuration
The adapter supports sharing configuration files with other adapters by using the namespace `shelly`.
Within `nodes` each Shelly device configuration is mapped. The key is the part of the topic directly after `shellies/`. A custom name can be given under _Internet & Security → ADVANCED - DEVELOPER SETTINGS → Custom MQTT prefix_ in the Shelly device web interface.

Each device can have their `relay0` and `relay1` mapped to a lamp. Mapping `relay1` on a Shelly 1 or another device without a second relay will announce device, but any action will have no effect.

```yaml
shelly:
  nodes:
    livingroom-lightswitch:
      relay0:
        device:
          room: Living room
          name: Ceiling lamp
        as: Lamp
      relay1:
        device:
          room: Living room
          name: Wall lamp
        as: Lamp

    hall-lightswitch:
      relay0:
        device:
          room: Hall
          name: Ceiling lamp
        as: Lamp
      relay1:
        device:
          room: Hall
          name: Ceiling fan
        as: Switch
```
