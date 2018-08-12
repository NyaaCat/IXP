# Item eXchange Portal

The IXP plugin is used to exchange item across multiple servers.

* Send items in hand to a specific server via an outbound sign (IXP sign).
* Receive item in that server via an inbound sign (IXP sign).
* Send items to another player in another server with a password.

### Commands & Permissions

| Command  | Permission | Description |
| --- | --- | --- |
| `/ixp sign create send [server-id]` | `ixp.admin` | Register a send out (outbound) IXP sign that send items to the specific server |
| `/ixp sign create receive` | `ixp.admin` | Register a receive IXP sign that receives items in current server |
| `/ixp sign remove` | `ixp.admin` | Unregister the facing IXP sign |
| `/ixp inv acquire [player]` | `ixp.admin` | Acquire all items of a specific player (sender) |
| `/ixp inv clear [player]` | `ixp.admin` | Clear all items of a specific player (sender) |
| `/ixp pass [password]` | `ixp.user` | Enter password if needed |

### Configuration

`config.yml`

```
lang: en
id: act  # local server id
psk: some_password_1  # local server pre-shared key
http:
  port: 7363  # S E N D
server-ids:
  nyaa:
    address: 192.168.1.101:7363
    psk: some_password_2
    enabled: true
  kedama:
    address: 192.168.1.102:7363
    psk: some_password_3
    enabled: true
  minigame:
    address: 192.168.1.103:1063
    psk: some_password_4
    enabled: false  # disable send to this server, receive is still available.
fee:   # all fees if enabled with HEH, store to system balance. If enabled without HEH, remove from player balance directly.
  send: 10  # per-slot items, this is only a default value
  receive: 100  # per-slot items, this is only a default value
misc:
  password-length: 16 # maximum characters allowed for passwords
  password-timeout: 30  # seconds after first interaction with sign
  slot-limit: 16  # item slots a player (sender) can use
```

And a local SQLite database file (or use other database that NC supports) to store signs and items information.

### Signs

Left `fee` section as empty for default value, or enter a value to override.

* Outbound sign

```
[IXP]
SEND
<server-id>
<fee>
```

E.g.,

```
[IXP]
SEND
nyaa
20
```

Can be shown as:

```
[IXP]
SEND
Send to: nyaa
Fee: 20
```

* Inbound sign

```
[IXP]
RECEIVE
<anything>
<fee>
```

E.g.,

```
[IXP]
RECEIVE
Nyaa Post Service
100
```

### Usage

* Sending items without password

Player hold the items and right click on the SEND sign twice to send to specific server (ignore password prompt).

(Items saved to remote server's plugin database).

* Acquiring items without password

Player right click on the RECEIVE sign twice to acquire all unprotected items (ignore password prompt, store to inventory or ender chest).

(Items removed from local plugin database).

* Sending items with password

1. Player hold the items and right click on the SEND sign to specific server.
2. Plugin prompt for password in 10 seconds.
3. Player offer a password to protect the items.

(Items saved to remote server's plugin database with a password).

* Acquiring items with password

1. Player right click on the RECEIVE sign.
2. Plugin prompt for password.
3. Player enters password, and receive the corresponding items. If no password matches (wrong password), return no items found.

(Items removed from local plugin database).

### Additional information

Designed feature:

* If multiple players send items to one server with the same password, one can acquire all of them with a single request and correct password.
* Items with no password provided, can be acquired only by the same player (uuid).
* All HTTP requests should be async.

### API

* Sending items

HTTP PUT `http://<remote server endpoint>/ix/<remote-server-id>/<trans-id>`

* `remote-server-id`: the target server-id
* `trans-id`: random uid string as transaction id, shared between the two servers

Header:

`x-ixp-psk`: `<remote server psk>`

Payload:

```
origin: <local server id>
sender: <sender player uuid>
item: <item NBT data>
password: <password string or empty>
timestamp: <unix timestamp when sending the item>
```

Use HTTP status code as return values.

* `201` - Item received in good order
* `400` - Bad Request
* `401` - Authentication Failure
* `500` - Internal plugin error
* `503` - Service unavailable - You don't have enough slots on the remote server

When request connection refused or timed out, return error and store the item back to the sender in origin server.

*** Another solution: share a dedicated database instance.
