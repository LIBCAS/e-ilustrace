# Projekt Česká knižní ilustrace v raném novověku (e-ilustrace)

Hlavním cílem projektu je vytvořit v ČR zcela nové badatelské prostředí, které umožní nejen online zpřístupnit obrazový materiál širší veřejnosti, ale také díky využití digitálních nástrojů nabídne nové možnosti výzkumu v oblasti starší knižní kultury.

Pro zpracování a základní zpřístupnění obrazového materiálu byly zvoleny již zmíněné databáze KPS a BCBT. Jednak z toho důvodu, že pro tvorbu nového databázového řešení nedisponujeme ani časovým ani finančním pokrytím, ale především proto, že je více než vhodné využít zkušenosti se sdílením dat a tzv. zpracovatelské workflow z předchozího projektu. Tento postup zároveň umožní další obohacování bibliografických databází, přičemž cílené databázové zpracování knižní ilustrace z určitého, jasně vymezeného období je stejně jako zamýšlené využití mezinárodního klasifikačního systému ICONCLASS v našem prostředí naprostou novinkou.

Návrh a celkové řešení nového badatelského prostředí zajišťuje technologická společnost InQool, která již s Knihovnou AV ČR spolupracovala na příklad v projektu INDIHU, jehož cílem byl vývoj nástrojů pro digital humanities. Opět se tak jedná o využití dřívější dobré praxe a využití znalostí z dané oblasti. Tentokrát ovšem společnost InQool neplní pouze roli dodavatele řešení, nýbrž je v pozici plnohodnotného projektového partnera. Pro tvorbu virtuálního badatelského rozhraní tak bude využívat jak vlastní know-how, tak dostupné programy s otevřeným kódem a především digitální nástroje vyvíjené oxfordskou skupinou Visual Geometry Group, která projektu díky dobrým kolegiálním vztahům přislíbila poskytnout metodickou a technickou podporu.



## Local development

1. copy `docker-compose.override.backend.yml` to `docker-compose.override.yml`
2. create docker volume `docker volume create --driver local -o o=bind -o type=none -o device=$PWD/data eil_data`
3. run images `docker-compose up -d`

## VISE commit
- https://gitlab.com/vgg/vise 12efcec301a5b2ca3258257ee2721286e5116d45 

## VISE custom code edits
1. Metadata grouping: ```vise/code/src/www/project_filelist.js``` uncomment ```search_form.appendChild(groupby_select);``` and ```search_form.appendChild(clear_link);```
2. User external search "black boxes": ```vise/code/src/www/project_external_search.js``` add ```selected_image_dim = [e.target.naturalWidth, e.target.naturalHeight];``` on line 195
3. Modified ```vise/code/src/vise/project_manager.cc``` and ```vise/code/src/vise/project_manager_test.cc``` for random filelist ordering
