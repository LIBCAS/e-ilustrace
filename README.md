# Projekt Česká knižní ilustrace v raném novověku (e-ilustrace)

Hlavním cílem projektu je vytvořit v ČR zcela nové badatelské prostředí, které umožní nejen online zpřístupnit obrazový materiál širší veřejnosti, ale také díky využití digitálních nástrojů nabídne nové možnosti výzkumu v oblasti starší knižní kultury.

Pro zpracování a základní zpřístupnění obrazového materiálu byly zvoleny již zmíněné databáze KPS a BCBT. Jednak z toho důvodu, že pro tvorbu nového databázového řešení nedisponujeme ani časovým ani finančním pokrytím, ale především proto, že je více než vhodné využít zkušenosti se sdílením dat a tzv. zpracovatelské workflow z předchozího projektu. Tento postup zároveň umožní další obohacování bibliografických databází, přičemž cílené databázové zpracování knižní ilustrace z určitého, jasně vymezeného období je stejně jako zamýšlené využití mezinárodního klasifikačního systému ICONCLASS v našem prostředí naprostou novinkou.

Návrh a celkové řešení nového badatelského prostředí zajišťuje technologická společnost InQool, která již s Knihovnou AV ČR spolupracovala na příklad v projektu INDIHU, jehož cílem byl vývoj nástrojů pro digital humanities. Opět se tak jedná o využití dřívější dobré praxe a využití znalostí z dané oblasti. Tentokrát ovšem společnost InQool neplní pouze roli dodavatele řešení, nýbrž je v pozici plnohodnotného projektového partnera. Pro tvorbu virtuálního badatelského rozhraní tak bude využívat jak vlastní know-how, tak dostupné programy s otevřeným kódem a především digitální nástroje vyvíjené oxfordskou skupinou Visual Geometry Group, která projektu díky dobrým kolegiálním vztahům přislíbila poskytnout metodickou a technickou podporu.



## Local development

1. copy `docker-compose.override.backend.yml` to `docker-compose.override.yml`
2. create docker volume `docker volume create --driver local -o o=bind -o type=none -o device=$PWD/data eil_data`
3. run images `docker-compose up -d`


