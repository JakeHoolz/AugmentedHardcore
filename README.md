# AugmentedHardcore

AugmentedHardcore is a [Spigot](https://www.spigotmc.org/) plugin that enhances Minecraft's hardcore mode with lives, death bans, revives, and progression mechanics.

## Build

This project uses Maven and requires Java 8 or newer. To build the plugin run:

```bash
mvn package
```

The shaded jar will be created in `target/`. Copy the jar to your server's `plugins` directory and restart the server.

## Usage

On first run the plugin will generate its configuration files. Adjust `config.yml` and `messages.yml` to customize behaviour.

Handy commands include:

- `/augmentedhardcore` – show help and plugin information
- `/lives` – view your remaining lives
- `/revive <player>` – revive a death‑banned player

See the in‑game help for the complete command list and permissions.

## Contributing

Contributions are welcome! If you would like to help:

1. Fork the repository and create a feature branch.
2. Make your changes following the existing style.
3. Run `mvn test` to make sure the project builds.
4. Open a pull request with a clear description of your work.

Please open an issue to discuss major changes before starting.

