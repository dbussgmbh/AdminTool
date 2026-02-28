# Vaadin: Dynamisches Plugin-Routing (B)

Hier gibt es **eine** Host-Route:
- `/p/:pluginId/*` -> `PluginHostView`

Plugins liefern keine `@Route`, sondern implementieren `PluginUi` und werden via `ServiceLoader` aus externen JARs geladen.

## Build & Run

Java 21+, Maven 3.9+

```bash
mvn clean package
mvn -pl app spring-boot:run
```

## Plugins zur Laufzeit hinzufügen

1) neues Plugin-JAR nach `app/plugins/` kopieren  
2) in der App: **Plugin Manager** -> **Plugins neu laden**  
3) Seite reloadet, Menü zeigt neuen Eintrag
