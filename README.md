# ListaSerwery.pl Plugin

Oficjalny plugin do integracji Twojego serwera Minecraft z portalem [listaserwery.pl](https://listaserwery.pl). Umożliwia graczom automatyczne odbieranie nagród za głosy oddane na serwer.

## ✨ Funkcje

-   **Automatyczne nagrody:** Automatycznie przyznawaj nagrody graczom, którzy zagłosowali.
-   **Prosta konfiguracja:** Łatwy w użyciu plik `config.yml`.
-   **Wiadomości:** Możliwość pełnej personalizacji wiadomości z domyślnymi wartościami zapobiegającymi błędom.
-   **Komenda w grze:** Gracze mogą odebrać nagrodę za pomocą prostej komendy `/odbierz`.
-   **Inteligentne sprawdzanie:** Plugin informuje gracza, jeśli już odebrał nagrodę i kiedy będzie mógł zagłosować ponownie.

## ⚙️ Instalacja

1.  Pobierz najnowszą wersję pluginu z [sekcji Releases na GitHubie](https://github.com/ListaSerwery/ListaSerweryPlugin/releases).
2.  Umieść pobrany plik `.jar` w folderze `plugins` na swoim serwerze.
3.  Uruchom serwer, aby wygenerować plik konfiguracyjny `config.yml`.
4.  Zatrzymaj serwer i przejdź do konfiguracji.

## 🔧 Konfiguracja

Otwórz plik `plugins/ListaSerweryPlugin/config.yml` i uzupełnij go swoimi danymi z panelu [listaserwery.pl](https://listaserwery.pl).

```yaml
# Ustawienia komendy do odbierania nagród
command:
  name: "odbierz"
  aliases: ["odbierznagrode", "vote"]
  cooldown-seconds: 60

# Twój klucz API. Znajdziesz go na stronie listaserwery.pl w zakładce "Moje serwery".
api-key: "ZMIEN_MNIE"

# ID Twojego serwera. Znajdziesz go na stronie listaserwery.pl w zakładce "Moje serwery".
server-id: "ZMIEN_MNIE"

# Komendy do wykonania po udanym odebraniu nagrody.
# %player% - zostaje zamienione na nick gracza
rewards:
  commands:
    - "give %player% diamond 1"
    - "say Gracz %player% odebrał nagrodę za głos na listaserwery.pl!"

# Wiadomości wysyłane do gracza
messages:
  prefix: "&8[&bListaSerwery&8] "
  checking: "&7Sprawdzam dostępność nagrody..."
  success: "&aPomyślnie odebrano nagrodę za głos! Dziękujemy."
  no-vote: "&cNie masz żadnych nieodebranych nagród, lub głos nie został jeszcze zarejestrowany. Zazwyczaj trwa to około minuty od zagłosowania."
  invalid-setup: "&cPlugin nie jest poprawnie skonfigurowany. Skontaktuj się z administracją."
  api-error: "&cWystąpił błąd podczas łączenia z API listaserwery.pl. Spróbuj ponownie później."
  rate-limit: "&cZbyt wiele zapytań. Odczekaj chwilę przed ponowną próbą."
  already-claimed: "&cOdebrałeś już nagrodę za ostatni głos! Następny głos możliwy za: &e%timeLeft%"
  vote-link: "&aKliknij tutaj, aby zaglosowac!"
  cooldown: "&cMusisz odczekać jeszcze &e%seconds% &csekund przed ponownym użyciem komendy."

```

-   `api-key`: Twój unikalny klucz API dostępny w panelu serwera na listaserwery.pl.
-   `server-id`: Unikalne ID Twojego serwera, również dostępne w panelu.
-   `rewards.commands`: Lista komend, które zostaną wykonane przez konsolę po odebraniu nagrody. Możesz użyć zmiennej `%player%`.

Po zakończeniu konfiguracji uruchom serwer ponownie.

## 💬 Komendy

-   `/odbierz` - Główna komenda dla graczy, służąca do odebrania nagrody za głos.
---
*Plugin stworzony do integracji z portalem [listaserwery.pl](https://listaserwery.pl)*





