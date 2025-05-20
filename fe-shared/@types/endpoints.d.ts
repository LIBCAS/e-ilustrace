/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.34.976 on 2024-03-27 15:28:49.

export namespace Backend {

  interface Author extends DatedObject<Author>, Person, Authority {
    fullName: string;
    birthYear: string;
    deathYear: string;
  }

  interface AuthorCreate extends DatedObjectCreate<Author>, View {
  }

  interface AuthorDetail extends DatedObjectDefault<Author>, View, Person, Authority {
    fullName: string;
    birthYear: string;
    deathYear: string;
  }

  interface AuthorFacet extends DatedObjectDefault<Author>, View {
    fullName: string;
  }

  interface AuthorList extends DatedObjectDefault<Author>, View {
    fullName: string;
    authorityCode: string;
  }

  interface AuthorUpdate extends DatedObjectUpdate<Author>, View {
  }

  interface RecordAuthor extends DomainObject<RecordAuthor> {
    author: Author;
    roles: MarcRole[];
    fromBook: boolean;
    fromIllustration: boolean;
    mainAuthor: boolean;
  }

  interface RecordAuthorCreate extends DomainObjectCreate<RecordAuthor>, View {
  }

  interface RecordAuthorDetail extends DomainObjectDefault<RecordAuthor>, View {
    author: AuthorDetail;
    roles: MarcRole[];
    fromBook: boolean;
    fromIllustration: boolean;
    mainAuthor: boolean;
  }

  interface RecordAuthorFacet extends DomainObjectDefault<RecordAuthor>, View {
    author: AuthorFacet;
    roles: MarcRole[];
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface RecordAuthorList extends DomainObjectDefault<RecordAuthor>, View {
    author: AuthorList;
    roles: MarcRole[];
    fromBook: boolean;
    fromIllustration: boolean;
    mainAuthor: boolean;
  }

  interface RecordAuthorRef {
    id: string;
  }

  interface RecordAuthorUpdate extends DomainObjectUpdate<RecordAuthor>, View {
  }

  interface Authority {
    authorityCode: string;
  }

  interface EnumReference<E> {
    id: string;
    name: string;
  }

  interface LabeledEnum<E> extends Labeled {
  }

  interface TypeEnum<E, ROOT> {
  }

  interface DownloadReference extends DomainObject<DownloadReference> {
    created: Date;
    url: string;
    referencedAttribute: ImageForDownload;
    record: RecordUnion;
    downloaded: boolean;
    failed: boolean;
  }

  interface EilExceptionCode extends ExceptionCode {
  }

  interface Exhibition extends DatedObject<Exhibition> {
    name: string;
    description: string;
    published: Date;
    items: ExhibitionItem[];
    user: User;
    radio: Radio;
    itemsCount: number;
  }

  interface ExhibitionCreate extends DatedObjectCreate<Exhibition>, View {
    name: string;
    description: string;
    items: ExhibitionItemCreate[];
    user: UserRef;
    radio: Radio;
  }

  interface ExhibitionDefault extends DatedObjectDefault<Exhibition>, View {
    name: string;
    description: string;
    radio: Radio;
  }

  interface ExhibitionDetail extends DatedObjectDefault<Exhibition>, View {
    name: string;
    description: string;
    published: Date;
    items: ExhibitionItemDetail[];
    user: UserEssential;
    radio: Radio;
    itemsCount: number;
  }

  interface ExhibitionEssential extends DatedObjectDefault<Exhibition>, View {
    name: string;
    description: string;
    items: ExhibitionItemExternalUpdate[];
    user: UserRef;
    radio: Radio;
  }

  interface ExhibitionList extends DatedObjectDefault<Exhibition>, View {
    name: string;
    description: string;
    published: Date;
    items: ExhibitionItemDetail[];
    user: UserEssential;
    radio: Radio;
    itemsCount: number;
  }

  interface ExhibitionRef {
    id: string;
  }

  interface ExhibitionUpdate extends DatedObjectUpdate<Exhibition>, View {
    name: string;
    description: string;
    items: ExhibitionItemExternalUpdate[];
    radio: Radio;
  }

  interface ExhibitionItem extends DatedObject<ExhibitionItem> {
    description: string;
    illustration: Illustration;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemCreate extends DatedObjectCreate<ExhibitionItem>, View {
    description: string;
    illustration: IllustrationRef;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemDefault extends DatedObjectDefault<ExhibitionItem>, View {
    description: string;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemDetail extends DatedObjectDefault<ExhibitionItem>, View {
    description: string;
    illustration: IllustrationExhibition;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemExternalUpdate extends DatedObjectDefault<ExhibitionItem>, View {
    description: string;
    illustration: IllustrationRef;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemList extends DatedObjectDefault<ExhibitionItem>, View {
    description: string;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemRef {
    id: string;
  }

  interface ExhibitionItemUpdate extends DatedObjectUpdate<ExhibitionItem>, View {
    description: string;
    name: string;
    year: string;
    preface: boolean;
  }

  interface Genre extends DatedObject<Genre>, Authority {
    name: string;
  }

  interface GenreCreate extends DatedObjectCreate<Genre>, View {
  }

  interface GenreDetail extends DatedObjectDefault<Genre>, View, Authority {
    name: string;
  }

  interface GenreList extends DatedObjectDefault<Genre>, View {
    name: string;
  }

  interface GenreRef {
    id: string;
  }

  interface GenreUpdate extends DatedObjectUpdate<Genre>, View {
  }

  interface IconclassCategory extends DatedObject<IconclassCategory> {
    code: string;
    name: string;
    url: string;
  }

  interface IconclassCategoryCreate extends DatedObjectCreate<IconclassCategory>, View {
    code: string;
    name: string;
    url: string;
  }

  interface IconclassCategoryDefault extends DatedObjectDefault<IconclassCategory>, View {
    code: string;
    name: string;
    url: string;
  }

  interface IconclassCategoryRef {
    id: string;
  }

  interface IconclassCategorySpecificationDto {
    txt: IconclassTxtAttributeDto;
  }

  interface IconclassTxtAttributeDto {
    en: string;
    de: string;
  }

  interface IconclassCategotyInitializer extends DatedInitializer<IconclassCategory, any> {
    repository: any;
    iconclassService: any;
    iconclassRepository: any;
  }

  interface NotificationTemplateInitializer extends DatedInitializer<NotificationTemplate, any> {
    repository: any;
    resourceLoader: ResourceLoader;
  }

  interface UserInitializer extends DatedInitializer<User, any> {
    repository: any;
    passwordEncoder: any;
    userRepository: any;
  }

  interface Institution extends DatedObject<Institution>, Authority {
    name: string;
  }

  interface InstitutionCreate extends DatedObjectCreate<Institution>, View {
  }

  interface InstitutionDetail extends DatedObjectDefault<Institution>, View, Authority {
    name: string;
  }

  interface InstitutionList extends DatedObjectDefault<Institution>, View {
    name: string;
    authorityCode: string;
  }

  interface InstitutionRef {
    id: string;
  }

  interface InstitutionUpdate extends DatedObjectUpdate<Institution>, View {
  }

  interface RecordInstitution extends DomainObject<RecordInstitution> {
    institution: Institution;
    roles: MarcRole[];
    mainWorkshop: boolean;
  }

  interface RecordInstitutionDefault extends DomainObjectDefault<RecordInstitution>, View {
    institution: InstitutionList;
    roles: MarcRole[];
    mainWorkshop: boolean;
  }

  interface Keyword extends DomainObject<Keyword> {
    label: string;
  }

  interface KeywordCreate extends DomainObjectCreate<Keyword>, View {
    label: string;
  }

  interface KeywordDetail extends DomainObjectDefault<Keyword>, View {
    label: string;
  }

  interface KeywordList extends DomainObjectDefault<Keyword>, View {
    label: string;
  }

  interface KeywordRef {
    id: string;
  }

  interface KeywordUpdate extends DomainObjectUpdate<Keyword>, View {
    label: string;
  }

  interface InfoJsonDto {
    "@context": string;
    "@id": string;
    protocol: string;
    width: number;
    height: number;
  }

  interface Canvas {
    "@id": string;
    "@type": string;
    images: Image[];
    height: number;
    width: number;
    label: string;
  }

  interface Image {
    "@id": string;
    "@type": string;
    motivation: string;
    on: string;
    resource: Resource;
  }

  interface Manifest {
    "@context": string;
    "@type": string;
    "@id": string;
    label: string;
    description: string;
    sequences: Sequence[];
  }

  interface Resource {
    "@id": string;
    "@type": string;
    format: string;
    height: number;
    width: number;
    service: any;
  }

  interface Sequence {
    "@id": string;
    "@type": string;
    canvases: Canvas[];
  }

  interface EmailNotification extends Notification {
    type: "EMAIL";
    email: string;
    html: boolean;
    mail: Mail;
  }

  interface EmailNotificationBase extends NotificationBase, View {
    type: "EMAIL";
    email: string;
    html: boolean;
    mail: MailDetail;
  }

  interface EmailNotificationDetail extends NotificationDetail, View {
    type: "EMAIL";
    email: string;
    html: boolean;
    mail: MailDetail;
  }

  interface EmailNotificationList extends NotificationList, View {
    type: "EMAIL";
    email: string;
  }

  interface EmailNotificationReceivedDetail extends NotificationReceivedDetail, View {
    type: "EMAIL";
    email: string;
  }

  interface EmailNotificationReceivedList extends NotificationReceivedList, View {
    type: "EMAIL";
    email: string;
  }

  interface Notification extends DatedObject<Notification> {
    type: "EMAIL";
    event: NotificationEvent;
    recipient: User;
    subject: string;
    content: string;
  }

  interface NotificationBase extends DatedObjectDefault<Notification>, AbstractView {
    type: "EMAIL";
    event: NotificationEvent;
    recipient: UserDetail;
    subject: string;
    content: string;
  }

  interface NotificationDetail extends DatedObjectDefault<Notification>, AbstractView {
    type: "EMAIL";
    event: NotificationEvent;
    recipient: UserDetail;
    subject: string;
    content: string;
  }

  interface NotificationList extends DatedObjectDefault<Notification>, AbstractView {
    type: "EMAIL";
    event: NotificationEvent;
    recipient: UserDetail;
    subject: string;
  }

  interface NotificationProperties {
    app: App;
  }

  interface App {
    name: string;
  }

  interface NotificationReceivedDetail extends DatedObjectIdentified<Notification>, AbstractView {
    type: "EMAIL";
    recipient: UserDetail;
    subject: string;
    content: string;
  }

  interface NotificationReceivedList extends DatedObjectIdentified<Notification>, AbstractView {
    type: "EMAIL";
    event: NotificationEvent;
    subject: string;
    content: string;
  }

  interface NotificationSender {
    notificationTemplateRepository: any;
    notificationRepository: any;
    notificationGenerator: NotificationGenerator;
    mailQueue: MailQueue;
    notificationProperties: NotificationProperties;
  }

  interface NotificationGenerator {
    templateCache: TemplateCache;
  }

  interface NotificationTemplateError extends EasException {
  }

  interface TemplateCache {
    freemarkerConfiguration: Configuration;
  }

  interface CacheItem {
  }

  interface EmailNotificationTemplate extends NotificationTemplate {
    type: "EMAIL";
    html: boolean;
  }

  interface EmailNotificationTemplateCreate extends NotificationTemplateCreate, View {
    type: "EMAIL";
  }

  interface EmailNotificationTemplateDetail extends NotificationTemplateDetail, View {
    type: "EMAIL";
    html: boolean;
  }

  interface EmailNotificationTemplateIdentified extends NotificationTemplateIdentified, View {
    type: "EMAIL";
  }

  interface EmailNotificationTemplateLabeled extends NotificationTemplateLabeled, View {
    type: "EMAIL";
  }

  interface EmailNotificationTemplateList extends NotificationTemplateList, View {
    type: "EMAIL";
  }

  interface EmailNotificationTemplateUpdate extends NotificationTemplateUpdate, View {
    type: "EMAIL";
  }

  interface NotificationTemplate extends AuthoredObject<NotificationTemplate>, Labeled {
    type: "EMAIL";
    version: number;
    name: string;
    event: NotificationEvent;
    subject: string;
    content: string;
    active: boolean;
  }

  interface NotificationTemplateCreate extends AuthoredObjectCreate<NotificationTemplate>, AbstractView {
    type: "EMAIL";
    name: string;
    event: NotificationEvent;
    subject: string;
    content: string;
  }

  interface NotificationTemplateDetail extends AuthoredObjectDefault<NotificationTemplate>, AbstractView, Labeled {
    type: "EMAIL";
    version: number;
    name: string;
    event: NotificationEvent;
    subject: string;
    content: string;
    active: boolean;
  }

  interface NotificationTemplateIdentified extends AuthoredObjectIdentified<NotificationTemplate>, AbstractView {
    type: "EMAIL";
  }

  interface NotificationTemplateLabeled extends AuthoredObjectIdentified<NotificationTemplate>, AbstractView, Labeled {
    type: "EMAIL";
    name: string;
  }

  interface NotificationTemplateList extends AuthoredObjectDefault<NotificationTemplate>, AbstractView, Labeled {
    type: "EMAIL";
    version: number;
    name: string;
    event: NotificationEvent;
    subject: string;
    active: boolean;
  }

  interface NotificationTemplateUpdate extends AuthoredObjectUpdate<NotificationTemplate>, AbstractView {
    type: "EMAIL";
    version: number;
    name: string;
    event: NotificationEvent;
    subject: string;
    content: string;
  }

  interface CommonModel extends NotificationTemplateModel {
    /**
     * Datum a čas odeslání notifikace
     */
    timestamp: Date;
  }

  interface EmailModel extends CommonModel {
    /**
     * Název aplikace
     */
    applicationName: string;
    /**
     * URL aplikace
     */
    applicationUrl: string;
  }

  interface NotificationTemplateModel {
  }

  interface TokenNotificationTemplateModel extends UserNotificationTemplateModel {
    /**
     * Token pro potvrzení
     */
    token: string;
  }

  interface UserNotificationTemplateModel extends EmailModel {
    /**
     * Uživatel
     */
    user: UserModel;
  }

  interface UserModel {
    /**
     * Jméno
     */
    firstName: string;
    /**
     * Příjmení
     */
    lastName: string;
    /**
     * E-mail
     */
    email: string;
    /**
     * Potvrzovací kód
     */
    token: string;
  }

  interface Person {
  }

  interface PublishingPlace extends DatedObject<PublishingPlace>, Authority {
    name: string;
    country: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface PublishingPlaceCreate extends DatedObjectCreate<PublishingPlace>, View {
  }

  interface PublishingPlaceDetail extends DatedObjectDefault<PublishingPlace>, View, Authority {
    name: string;
    country: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface PublishingPlaceFacet extends DatedObjectDefault<PublishingPlace>, View {
    name: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface PublishingPlaceList extends DatedObjectDefault<PublishingPlace>, View {
    name: string;
    country: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface PublishingPlaceRef {
    id: string;
  }

  interface PublishingPlaceUpdate extends DatedObjectUpdate<PublishingPlace>, View {
  }

  interface Record extends DatedObject<Record> {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    fixedLengthField: string;
    title: string;
    variantTitles: string[];
    physicalDescription: string | null;
    technique: string | null;
    dimensions: string | null;
    notes: Note[];
    contentNotes: string[];
    references: Reference[];
    subjectPersons: SubjectPerson[];
    subjectInstitutions: SubjectInstitution[];
    subjectEntries: SubjectEntry[];
    subjectPlaces: SubjectPlace[];
    keywords: Keyword[];
    genres: Genre[];
    links: Link[];
    owners: Owner[];
    publishingPlaces: PublishingPlace[];
    iconclass: IconclassCategory[];
    themes: Theme[];
    mainAuthor: RecordAuthor;
    mainWorkshop: RecordInstitution;
    coauthors: RecordAuthor[];
    coinstitutions: RecordInstitution[];
  }

  interface RecordCreate extends DatedObjectCreate<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
  }

  interface RecordDetail extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    fixedLengthField: string;
    title: string;
    variantTitles: string[];
    physicalDescription: string | null;
    technique: string | null;
    dimensions: string | null;
    notes: NoteDefault[];
    contentNotes: string[];
    references: ReferenceDefault[];
    subjectPersons: SubjectPersonDetail[];
    subjectInstitutions: SubjectInstitutionDetail[];
    subjectEntries: SubjectEntryDetail[];
    subjectPlaces: SubjectPlaceDetail[];
    keywords: Keyword[];
    genres: GenreDetail[];
    links: LinkDefault[];
    owners: OwnerDefault[];
    publishingPlaces: PublishingPlaceDetail[];
    iconclass: IconclassCategoryDefault[];
    themes: ThemeDefault[];
    mainAuthor: RecordAuthorDetail | null;
    mainWorkshop: RecordInstitutionDefault;
    coauthors: RecordAuthorDetail[];
    coinstitutions: RecordInstitutionDefault[];
  }

  interface RecordEssential extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    title: string;
    physicalDescription: string;
  }

  interface RecordExhibition extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    title: string;
    mainAuthor: RecordAuthorDetail;
  }

  interface RecordFacet extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    subjectPersons: SubjectPersonFacet[];
    subjectEntries: SubjectEntryFacet[];
    subjectPlaces: SubjectPlaceFacet[];
    keywords: Keyword[];
    publishingPlaces: PublishingPlaceFacet[];
  }

  interface RecordIdentified extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    fixedLengthField: string;
    title: string;
  }

  interface RecordIdentifier extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
  }

  interface RecordList extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    title: string;
    mainAuthor: RecordAuthorDetail;
    mainWorkshop: RecordInstitutionDefault;
  }

  interface RecordMarc extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    keywords: Keyword[];
    links: Link[];
    iconclass: IconclassCategory[];
    themes: Theme[];
  }

  interface RecordSources extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    subjectPersons: SubjectPerson[];
    subjectEntries: SubjectEntry[];
    subjectPlaces: SubjectPlace[];
    publishingPlaces: PublishingPlace[];
  }

  interface RecordUpdate extends DatedObjectUpdate<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    iconclass: IconclassCategoryRef[];
    themes: ThemeRef[];
  }

  interface RecordVise extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    publishingPlaces: PublishingPlaceDetail[];
    mainAuthor: RecordAuthorDetail;
    coauthors: RecordAuthorDetail[];
  }

  interface RecordXlsx extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    iconclass: IconclassCategoryRef[];
    themes: ThemeRef[];
  }

  interface Book extends Record {
    type: "BOOK";
    illustrations: Illustration[];
    publishingEntry: PublicationEntry;
    frontPageScan: File | null;
  }

  interface BookCreate extends RecordCreate, View {
    type: "BOOK";
  }

  interface BookDetail extends RecordDetail, View {
    type: "BOOK";
    illustrations: IllustrationEssential[];
    publishingEntry: PublicationEntryDefault | null;
    frontPageScan: FileDetail | null;
  }

  interface BookEssential extends RecordEssential, View {
    type: "BOOK";
    frontPageScan: FileDetail;
  }

  interface BookExhibition extends RecordExhibition, View {
    type: "BOOK";
  }

  interface BookFacet extends RecordFacet, View {
    type: "BOOK";
  }

  interface BookIdentified extends RecordIdentified, View {
    type: "BOOK";
    illustrations: IllustrationIdentified[];
  }

  interface BookIdentifier extends RecordIdentifier, View {
    type: "BOOK";
  }

  interface BookList extends RecordList, View {
    type: "BOOK";
    illustrations: IllustrationEssential[];
    publishingEntry: PublicationEntryDefault;
    frontPageScan: FileRef | null;
  }

  interface BookMarc extends RecordMarc, View {
    type: "BOOK";
    frontPageScan: File;
  }

  interface BookRef {
    id: string;
  }

  interface BookSources extends RecordSources, View {
    type: "BOOK";
  }

  interface BookUpdate extends RecordUpdate, View {
    type: "BOOK";
  }

  interface BookVise extends RecordVise, View {
    type: "BOOK";
  }

  interface BookXlsx extends RecordXlsx, View {
    type: "BOOK";
  }

  interface RecordFacetsDto {
    authors: AuthorFacet[];
    publishingPlaces: PublishingPlaceFacet[];
    subjectPlaces: SubjectPlaceFacet[];
    subjectPersons: SubjectPersonFacet[];
    subjectEntries: SubjectEntryFacet[];
    keywords: Keyword[];
    genres: Genre[];
  }

  interface RecordTypeDto {
    type: string;
  }

  interface RecordYearsDto {
    yearFrom: number;
    yearTo: number;
  }

  interface Illustration extends Record {
    type: "ILLUSTRATION";
    book: Book;
    printEntry: PublicationEntry | null;
    printingPlateEntry: PublicationEntry;
    defect: string;
    illustrationScan: File;
    pageScan: File;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
    viseFileId: string;
    viseIllScanCopied: Date;
    cantaloupeIllScanId: string;
    cantaloupeIllScanCopied: Date;
    cantaloupePageScanId: string;
    cantaloupePageScanCopied: Date;
  }

  interface IllustrationCreate extends RecordCreate, View {
    type: "ILLUSTRATION";
  }

  interface IllustrationDetail extends RecordDetail, View {
    type: "ILLUSTRATION";
    book: BookIdentified | null;
    printEntry: PublicationEntryDefault | null;
    printingPlateEntry: PublicationEntryDefault | null;
    defect: string | null;
    illustrationScan: FileDetail | null;
    pageScan: FileDetail | null;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
    viseFileId: string;
    cantaloupeIllScanId: string;
    cantaloupePageScanId: string;
  }

  interface IllustrationEssential extends RecordEssential, View {
    type: "ILLUSTRATION";
    book: BookRef;
    illustrationScan: FileDetail;
    pageScan: FileDetail;
    viseFileId: string;
    viseIllScanCopied: Date;
    cantaloupeIllScanId: string;
    cantaloupeIllScanCopied: Date;
    cantaloupePageScanId: string;
    cantaloupePageScanCopied: Date;
  }

  interface IllustrationExhibition extends RecordExhibition, View {
    type: "ILLUSTRATION";
    book: BookExhibition;
    illustrationScan: FileRef;
    pageScan: FileRef;
  }

  interface IllustrationFacet extends RecordFacet, View {
    type: "ILLUSTRATION";
  }

  interface IllustrationIdentified extends RecordIdentified, View {
    type: "ILLUSTRATION";
    book: BookRef;
  }

  interface IllustrationIdentifier extends RecordIdentifier, View {
    type: "ILLUSTRATION";
  }

  interface IllustrationList extends RecordList, View {
    type: "ILLUSTRATION";
    printEntry: PublicationEntryDefault | null;
    illustrationScan: FileRef | null;
    pageScan: FileRef | null;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
    viseFileId: string;
    cantaloupeIllScanId: string;
    cantaloupePageScanId: string;
  }

  interface IllustrationMarc extends RecordMarc, View {
    type: "ILLUSTRATION";
    illustrationScan: File;
    pageScan: File;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
    viseFileId: string;
    viseIllScanCopied: Date;
    cantaloupeIllScanId: string;
    cantaloupeIllScanCopied: Date;
    cantaloupePageScanId: string;
    cantaloupePageScanCopied: Date;
  }

  interface IllustrationRef {
    id: string;
  }

  interface IllustrationSources extends RecordSources, View {
    type: "ILLUSTRATION";
  }

  interface IllustrationUpdate extends RecordUpdate, View {
    type: "ILLUSTRATION";
  }

  interface IllustrationVise extends RecordVise, View {
    type: "ILLUSTRATION";
    viseFileId: string;
    cantaloupeIllScanId: string;
  }

  interface IllustrationXlsx extends RecordXlsx, View {
    type: "ILLUSTRATION";
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
  }

  interface XlsxReader {
    themeRepository: any;
    recordRepository: any;
    iconclassRepository: any;
    transactionTemplate: TransactionTemplate;
    iconclassService: any;
    resourceLoader: ResourceLoader;
  }

  interface XlsxIllustrationDto {
    iconclassCodes: string[];
    identifier: string;
    themes: string[];
    iconclassState: string;
  }

  interface Link extends DomainObject<Link> {
    url: string;
    description: string;
    record: RecordUnion;
  }

  interface LinkDefault extends DomainObjectDefault<Link>, View {
    url: string;
    description: string;
    record: RecordIdentifiedUnion;
  }

  interface Note extends DomainObject<Note> {
    title: string;
    text: string;
  }

  interface NoteDefault extends DomainObjectDefault<Note>, View {
    title: string;
    text: string;
  }

  interface Owner extends DomainObject<Owner> {
    name: string;
    signature: string;
  }

  interface OwnerDefault extends DomainObjectDefault<Owner>, View {
    name: string;
    signature: string;
  }

  interface PublicationEntry extends DomainObject<PublicationEntry> {
    placesOfPublication: string[];
    originators: string[];
    date: string;
  }

  interface PublicationEntryDefault extends DomainObjectDefault<PublicationEntry>, View {
    placesOfPublication: string[];
    originators: string[];
    date: string;
  }

  interface Reference extends DomainObject<Reference> {
    workTitle: string;
    location: string;
  }

  interface ReferenceDefault extends DomainObjectDefault<Reference>, View {
    workTitle: string;
    location: string | null;
  }

  interface EilFormAuthenticationProvider {
    userCache: any;
    forcePrincipalAsString: boolean;
    hideUserNotFoundExceptions: boolean;
    preAuthenticationChecks: any;
    postAuthenticationChecks: any;
    authoritiesMapper: any;
    passwordEncoder: any;
    useTwoFactorAuth: boolean;
    twoFactorService: any;
    userRepository: any;
    userToUserConverter: UserToUserConverter;
    messageSource: MessageSource;
  }

  interface UserChecker {
  }

  interface UserToUserConverter {
  }

  interface PasswordEncoderFactory {
  }

  interface Token extends DomainObject<Token> {
    user: User;
    expiration: Date;
    used: boolean;
  }

  interface Selection extends DatedObject<Selection> {
    items: SelectionItem[];
    user: User;
  }

  interface SelectionCreate extends DatedObjectCreate<Selection>, View {
  }

  interface SelectionDefault extends DatedObjectDefault<Selection>, View {
  }

  interface SelectionDetail extends DatedObjectDefault<Selection>, View {
    items: SelectionItemDetail[];
    user: UserRef;
  }

  interface SelectionList extends DatedObjectDefault<Selection>, View {
  }

  interface SelectionRef {
    id: string;
  }

  interface SelectionUpdate extends DatedObjectUpdate<Selection>, View {
    items: SelectionItemExternalUpdate[];
  }

  interface SelectionUserFind extends DatedObjectDefault<Selection>, View {
    items: SelectionItemExternalUpdate[];
    user: UserRef;
  }

  interface SelectionDto {
    items: SelectionItemRef[];
  }

  interface SelectionUpdateDto {
    illustrations: IllustrationRef[];
    books: BookRef[];
  }

  interface SelectionItem extends DomainObject<SelectionItem> {
    illustration: Illustration;
    book: Book;
    selection: Selection;
    mirador: boolean;
  }

  interface SelectionItemCreate extends DomainObjectCreate<SelectionItem>, View {
    mirador: boolean;
  }

  interface SelectionItemDefault extends DomainObjectDefault<SelectionItem>, View {
    mirador: boolean;
  }

  interface SelectionItemDetail extends DomainObjectDefault<SelectionItem>, View {
    illustration: IllustrationEssential;
    book: BookEssential;
    selection: SelectionRef;
    mirador: boolean;
  }

  interface SelectionItemExternalUpdate extends DomainObjectDefault<SelectionItem>, View {
    illustration: IllustrationRef;
    book: BookRef;
    selection: SelectionRef;
    mirador: boolean;
  }

  interface SelectionItemList extends DomainObjectDefault<SelectionItem>, View {
    mirador: boolean;
  }

  interface SelectionItemRef {
    id: string;
  }

  interface SelectionItemUpdate extends DomainObjectUpdate<SelectionItem>, View {
    mirador: boolean;
  }

  interface SubjectEntry extends DatedObject<SubjectEntry>, Authority {
    label: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectEntryCreate extends DatedObjectCreate<SubjectEntry>, View {
  }

  interface SubjectEntryDetail extends DatedObjectDefault<SubjectEntry>, View, Authority {
    label: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectEntryFacet extends DatedObjectDefault<SubjectEntry>, View {
    label: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectEntryList extends DatedObjectDefault<SubjectEntry>, View {
    label: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectEntryRef {
    id: string;
  }

  interface SubjectEntryUpdate extends DatedObjectUpdate<SubjectEntry>, View {
  }

  interface SubjectInstitution extends DatedObject<SubjectInstitution>, Authority {
    name: string;
  }

  interface SubjectInstitutionCreate extends DatedObjectCreate<SubjectInstitution>, View {
  }

  interface SubjectInstitutionDetail extends DatedObjectDefault<SubjectInstitution>, View, Authority {
    name: string;
  }

  interface SubjectInstitutionList extends DatedObjectDefault<SubjectInstitution>, View {
    name: string;
  }

  interface SubjectInstitutionRef {
    id: string;
  }

  interface SubjectInstitutionUpdate extends DatedObjectUpdate<SubjectInstitution>, View {
  }

  interface SubjectPerson extends DatedObject<SubjectPerson>, Person, Authority {
    fullName: string;
    birthYear: string;
    deathYear: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPersonCreate extends DatedObjectCreate<SubjectPerson>, View {
  }

  interface SubjectPersonDetail extends DatedObjectDefault<SubjectPerson>, View, Person, Authority {
    fullName: string;
    birthYear: string;
    deathYear: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPersonFacet extends DatedObjectDefault<SubjectPerson>, View {
    fullName: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPersonList extends DatedObjectDefault<SubjectPerson>, View, Authority {
    fullName: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPersonRef {
    id: string;
  }

  interface SubjectPersonUpdate extends DatedObjectUpdate<SubjectPerson>, View {
  }

  interface SubjectPlace extends DatedObject<SubjectPlace>, Authority {
    name: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPlaceCreate extends DatedObjectCreate<SubjectPlace>, View {
  }

  interface SubjectPlaceDetail extends DatedObjectDefault<SubjectPlace>, View, Authority {
    name: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPlaceFacet extends DatedObjectDefault<SubjectPlace>, View {
    name: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPlaceList extends DatedObjectDefault<SubjectPlace>, View {
    name: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPlaceRef {
    id: string;
  }

  interface SubjectPlaceUpdate extends DatedObjectUpdate<SubjectPlace>, View {
  }

  interface Theme extends DatedObject<Theme> {
    name: string;
  }

  interface ThemeCreate extends DatedObjectCreate<Theme>, View {
    name: string;
  }

  interface ThemeDefault extends DatedObjectDefault<Theme>, View {
    name: string;
  }

  interface ThemeRef {
    id: string;
  }

  interface ThemeCount {
    id: string;
    name: string;
    created: Date;
    updated: Date;
    deleted: Date;
    count: number;
  }

  interface User extends DatedObject<User> {
    email: string;
    firstName: string;
    lastName: string;
    password: string;
    role: EilRole;
    validated: boolean;
    fullName: string;
  }

  interface UserCreate extends DatedObjectCreate<User>, View {
    email: string;
    firstName: string;
    lastName: string;
    password: string;
  }

  interface UserDefault extends DatedObjectDefault<User>, View {
  }

  interface UserDetail extends DatedObjectDefault<User>, View {
    email: string;
    firstName: string;
    lastName: string;
    role: EilRole;
    validated: boolean;
    fullName: string;
  }

  interface UserEssential extends DatedObjectDefault<User>, View {
    firstName: string;
    lastName: string;
    fullName: string;
  }

  interface UserList extends DatedObjectDefault<User>, View {
    email: string;
    firstName: string;
    lastName: string;
    role: EilRole;
    validated: boolean;
    fullName: string;
  }

  interface UserRef {
    id: string;
  }

  interface UserUpdate extends DatedObjectUpdate<User>, View {
    firstName: string;
    lastName: string;
  }

  interface ChangePasswordDto {
    password: string;
  }

  interface ChangeRoleDto {
    userId: string;
    role: EilRole;
  }

  interface MarcCreateProcessor extends MarcProcessor {
    fieldMappers: DataFieldMapper[];
    recordRepository: any;
    importService: any;
    subjectPersonRepository: any;
    subjectInstitutionRepository: any;
    subjectEntryRepository: any;
    subjectPlaceRepository: any;
    genreRepository: any;
    publishingPlaceRepository: any;
    recordAuthorRepository: any;
    keywordRepository: any;
    downloadManager: any;
    bookToIllustration: Illustration;
  }

  interface MarcProcessor {
  }

  interface MarcUpdateProcessor extends MarcProcessor {
    fieldMappers: DataFieldMapper[];
    recordRepository: any;
    importService: any;
    subjectPersonRepository: any;
    subjectInstitutionRepository: any;
    subjectEntryRepository: any;
    subjectPlaceRepository: any;
    genreRepository: any;
    publishingPlaceRepository: any;
    recordAuthorRepository: any;
    keywordRepository: any;
    recordService: any;
    downloadManager: any;
    subjectService: any;
    publishingPlaceService: any;
    bookToIllustration: Illustration;
  }

  interface ManualImportDto {
    outputDir: string;
    reg: string;
    start: number;
    end: number;
  }

  interface Constants {
  }

  interface Utils {
  }

  interface DataFieldMapper {
    tag: string;
    illustrationMapper: boolean;
    bookMapper: boolean;
  }

  interface DataFieldMapper100 extends DataFieldMapper {
    authorRepository: any;
  }

  interface DataFieldMapper110 extends DataFieldMapper {
    institutionRepository: any;
  }

  interface DataFieldMapper245 extends DataFieldMapper {
  }

  interface DataFieldMapper246 extends DataFieldMapper {
  }

  interface DataFieldMapper260 extends DataFieldMapper {
  }

  interface DataFieldMapper300 extends DataFieldMapper {
  }

  interface DataFieldMapper500 extends DataFieldMapper {
  }

  interface DataFieldMapper505 extends DataFieldMapper {
  }

  interface DataFieldMapper510 extends DataFieldMapper {
  }

  interface DataFieldMapper591 extends DataFieldMapper {
  }

  interface DataFieldMapper600 extends DataFieldMapper {
    subjectPersonRepository: any;
  }

  interface DataFieldMapper610 extends DataFieldMapper {
    subjectInstitutionRepository: any;
  }

  interface DataFieldMapper650 extends DataFieldMapper {
    subjectEntryRepository: any;
  }

  interface DataFieldMapper651 extends DataFieldMapper {
    subjectPlaceRepository: any;
  }

  interface DataFieldMapper653 extends DataFieldMapper {
    keywordRepository: any;
  }

  interface DataFieldMapper655 extends DataFieldMapper {
    genreRepository: any;
  }

  interface DataFieldMapper700 extends DataFieldMapper {
    authorRepository: any;
  }

  interface DataFieldMapper710 extends DataFieldMapper {
    institutionRepository: any;
  }

  interface DataFieldMapper856 extends DataFieldMapper {
  }

  interface DataFieldMapper910 extends DataFieldMapper {
  }

  interface DataFieldMapper984 extends DataFieldMapper {
    publishingPlaceRepository: any;
  }

  interface DataFieldMapper264 {
    indicator: string;
  }

  interface DataFieldMapper2643 extends DataFieldMapper264, DataFieldMapper {
  }

  interface DataFieldMapper264X extends DataFieldMapper264, DataFieldMapper {
  }

  interface View {
  }

  interface Labeled {
    id: string;
    label: string;
  }

  interface ExceptionCode {
  }

  interface ResourceLoader {
    classLoader: ClassLoader;
  }

  interface Mail extends AuthoredObject<Mail> {
    subject: string;
    content: string;
    state: MailState;
    identifier: string;
    contentType: string;
    to: string;
    sent: boolean;
    error: string;
  }

  interface MailDetail extends AuthoredObjectDefault<Mail>, View {
    subject: string;
    content: string;
    state: MailState;
    identifier: string;
    contentType: string;
    to: string;
    sent: boolean;
    error: string;
  }

  interface AbstractView {
  }

  interface MailQueue {
    nextWaiting: Mail;
    store: MailStore;
  }

  interface Throwable extends Serializable {
    cause: Throwable;
    stackTrace: StackTraceElement[];
    message: string;
    suppressed: Throwable[];
    localizedMessage: string;
  }

  interface StackTraceElement extends Serializable {
    classLoaderName: string;
    moduleName: string;
    moduleVersion: string;
    methodName: string;
    fileName: string;
    lineNumber: number;
    className: string;
    nativeMethod: boolean;
  }

  interface EasException extends RuntimeException {
    code: string;
    httpStatus: number;
    logStrategy: LogStrategy;
    details: any;
    debugInfo: any;
    detailedMessage: string;
  }

  interface Configuration extends Configurable, Cloneable, ParserConfiguration {
    localizedLookup: boolean;
    outputFormatExplicitlySet: boolean;
    registeredCustomOutputFormats: OutputFormat[];
    fallbackOnNullLoopVariable: boolean;
    templateLoaderExplicitlySet: boolean;
    templateLookupStrategyExplicitlySet: boolean;
    templateNameFormatExplicitlySet: boolean;
    cacheStorageExplicitlySet: boolean;
    objectWrapperExplicitlySet: boolean;
    templateExceptionHandlerExplicitlySet: boolean;
    attemptExceptionReporterExplicitlySet: boolean;
    logTemplateExceptionsExplicitlySet: boolean;
    wrapUncheckedExceptionsExplicitlySet: boolean;
    localeExplicitlySet: boolean;
    defaultEncodingExplicitlySet: boolean;
    timeZoneExplicitlySet: boolean;
    defaultEncoding: string;
    recognizeStandardFileExtensionsExplicitlySet: boolean;
    templateLoader: TemplateLoader;
    cacheStorage: CacheStorage;
    templateLookupStrategy: TemplateLookupStrategy;
    /**
     * @deprecated
     */
    incompatibleEnhancements: string;
    /**
     * @deprecated
     */
    parsedIncompatibleEnhancements: number;
    templateUpdateDelayMilliseconds: number;
    templateConfigurations: TemplateConfigurationFactory;
    templateNameFormat: TemplateNameFormat;
    supportedBuiltInNames: any[];
    sharedVariableNames: any[];
    supportedBuiltInDirectiveNames: any[];
  }

  interface UserReference {
    id: string;
    name: string;
  }

  interface TenantReference {
    id: string;
    name: string;
  }

  interface File extends AuthoredObject<File> {
    name: string;
    contentType: string;
    size: number;
    permanent: boolean;
  }

  interface FileDetail extends AuthoredObjectDefault<File>, View {
    name: string;
    contentType: string;
    size: number;
    permanent: boolean;
  }

  interface FileRef {
    id: string;
  }

  interface TransactionTemplate extends DefaultTransactionDefinition, TransactionOperations, InitializingBean {
    transactionManager: PlatformTransactionManager;
  }

  interface MessageSource {
  }

  interface DatedObject<ROOT> extends DomainObject<ROOT>, Dated<ROOT> {
  }

  interface DatedObjectCreate<ROOT> extends DomainObjectCreate<ROOT>, AbstractView {
  }

  interface DatedObjectDefault<ROOT> extends DomainObjectDefault<ROOT>, AbstractView, Dated<ROOT> {
  }

  interface DatedObjectUpdate<ROOT> extends DomainObjectUpdate<ROOT>, AbstractView {
  }

  interface DomainObject<ROOT> extends Domain<ROOT>, Projectable<ROOT> {
  }

  interface DomainObjectCreate<ROOT> extends AbstractView, Projectable<ROOT> {
  }

  interface DomainObjectDefault<ROOT> extends AbstractView, Domain<ROOT>, Projectable<ROOT> {
  }

  interface DomainObjectUpdate<ROOT> extends AbstractView, Projectable<ROOT> {
  }

  interface DatedInitializer<ROOT, REPOSITORY> extends DomainInitializer<ROOT, REPOSITORY> {
  }

  interface ClassLoader {
  }

  interface DatedObjectIdentified<ROOT> extends DomainObjectIdentified<ROOT>, AbstractView {
    created: Date;
    deleted: Date;
  }

  interface MailStore extends AuthoredStore<Mail, Mail, QMail> {
    metaModel: QMail;
    nextWaiting: Mail;
  }

  interface Serializable {
  }

  interface RuntimeException extends Exception {
  }

  interface Configurable {
    parent: Configurable;
    locale: Locale;
    numberFormat: string;
    timeFormat: string;
    dateFormat: string;
    dateTimeFormat: string;
    timeZone: TimeZone;
    booleanFormat: string;
    classicCompatible: boolean;
    templateExceptionHandler: TemplateExceptionHandler;
    attemptExceptionReporter: AttemptExceptionReporter;
    arithmeticEngine: ArithmeticEngine;
    objectWrapper: ObjectWrapper;
    outputEncoding: string;
    outputEncodingSet: boolean;
    autoFlush: boolean;
    showErrorTips: boolean;
    newBuiltinClassResolver: TemplateClassResolver;
    truncateBuiltinAlgorithm: TruncateBuiltinAlgorithm;
    logTemplateExceptions: boolean;
    wrapUncheckedExceptions: boolean;
    customDateFormats: { [index: string]: TemplateDateFormatFactory };
    customNumberFormats: { [index: string]: TemplateNumberFormatFactory };
    autoImports: { [index: string]: string };
    autoIncludes: string[];
    lazyImports: boolean;
    lazyAutoImports: boolean;
    lazyAutoImportsSet: boolean;
    /**
     * @deprecated
     */
    settings: { [index: string]: any };
    customDateFormatsWithoutFallback: { [index: string]: TemplateDateFormatFactory };
    customNumberFormatsWithoutFallback: { [index: string]: TemplateNumberFormatFactory };
    showErrorTipsSet: boolean;
    numberFormatSet: boolean;
    objectWrapperSet: boolean;
    autoFlushSet: boolean;
    lazyImportsSet: boolean;
    timeZoneSet: boolean;
    localeSet: boolean;
    booleanFormatSet: boolean;
    timeFormatSet: boolean;
    dateFormatSet: boolean;
    autoIncludesSet: boolean;
    autoImportsSet: boolean;
    classicCompatibleAsInt: number;
    urlescapingCharsetSet: boolean;
    sqldateAndTimeTimeZoneSet: boolean;
    customDateFormatsSet: boolean;
    sqldateAndTimeTimeZone: TimeZone;
    urlescapingCharset: string;
    apibuiltinEnabled: boolean;
    attemptExceptionReporterSet: boolean;
    templateExceptionHandlerSet: boolean;
    autoImportsWithoutFallback: { [index: string]: string };
    dateTimeFormatSet: boolean;
    newBuiltinClassResolverSet: boolean;
    customAttributeNames: string[];
    classicCompatibleSet: boolean;
    autoIncludesWithoutFallback: string[];
    arithmeticEngineSet: boolean;
    customNumberFormatsSet: boolean;
    logTemplateExceptionsSet: boolean;
    apibuiltinEnabledSet: boolean;
    truncateBuiltinAlgorithmSet: boolean;
    wrapUncheckedExceptionsSet: boolean;
  }

  interface Locale extends Cloneable, Serializable {
  }

  interface TimeZone extends Serializable, Cloneable {
  }

  interface TemplateExceptionHandler {
  }

  interface AttemptExceptionReporter {
  }

  interface ArithmeticEngine {
  }

  interface ObjectWrapper {
  }

  interface TemplateClassResolver {
  }

  interface TruncateBuiltinAlgorithm {
  }

  interface TemplateDateFormatFactory extends TemplateValueFormatFactory {
  }

  interface TemplateNumberFormatFactory extends TemplateValueFormatFactory {
  }

  interface OutputFormat {
    name: string;
    mimeType: string;
    outputFormatMixingAllowed: boolean;
  }

  interface Version extends Serializable {
    major: number;
    minor: number;
    micro: number;
    extraInfo: string;
    buildDate: Date;
    gaecompliant: boolean;
  }

  interface TemplateLoader {
  }

  interface CacheStorage {
  }

  interface TemplateLookupStrategy {
  }

  interface TemplateConfigurationFactory {
    configuration: Configuration;
  }

  interface TemplateNameFormat {
  }

  interface Cloneable {
  }

  interface ParserConfiguration {
    outputFormat: OutputFormat;
    tagSyntax: number;
    tabSize: number;
    incompatibleImprovements: Version;
    interpolationSyntax: number;
    arithmeticEngine: ArithmeticEngine;
    whitespaceStripping: boolean;
    recognizeStandardFileExtensions: boolean;
    autoEscapingPolicy: number;
    namingConvention: number;
    strictSyntaxMode: boolean;
  }

  interface AuthoredObject<ROOT> extends DatedObject<ROOT>, Authored<ROOT> {
  }

  interface AuthoredObjectCreate<ROOT> extends DatedObjectCreate<ROOT>, AbstractView {
  }

  interface AuthoredObjectDefault<ROOT> extends DatedObjectDefault<ROOT>, AbstractView, Authored<ROOT> {
  }

  interface AuthoredObjectIdentified<ROOT> extends DatedObjectIdentified<ROOT>, AbstractView {
  }

  interface AuthoredObjectUpdate<ROOT> extends DatedObjectUpdate<ROOT>, AbstractView {
  }

  interface PlatformTransactionManager extends TransactionManager {
  }

  interface DefaultTransactionDefinition extends TransactionDefinition, Serializable {
    propagationBehaviorName: string;
    isolationLevelName: string;
  }

  interface TransactionOperations {
  }

  interface InitializingBean {
  }

  interface JPAQueryFactory extends JPQLQueryFactory {
  }

  interface Class<T> extends Serializable, GenericDeclaration, Type, AnnotatedElement {
  }

  interface QMail {
    _super: QAuthoredObject;
    content: any;
    contentType: any;
    created: any;
    createdBy: QUserReference;
    createdByTenant: QTenantReference;
    deleted: any;
    deletedBy: QUserReference;
    deletedByTenant: QTenantReference;
    error: any;
    id: any;
    identifier: any;
    sent: any;
    state: any;
    subject: any;
    to: any;
    updated: any;
    updatedBy: QUserReference;
    updatedByTenant: QTenantReference;
    root: any;
    metadata: any;
    annotatedElement: AnnotatedElement;
    type: Class<T>;
  }

  interface Exception extends Throwable {
  }

  interface TemplateValueFormatFactory {
  }

  interface TransactionManager {
  }

  interface TransactionDefinition {
    name: string;
    readOnly: boolean;
    timeout: number;
    isolationLevel: number;
    propagationBehavior: number;
  }

  interface Dated<ROOT> extends Domain<ROOT> {
    created: Date;
    updated: Date;
    deleted: Date;
  }

  interface Domain<ROOT> extends Projectable<ROOT> {
    id: string;
  }

  interface Projectable<ROOT> {
  }

  interface DomainInitializer<ROOT, REPOSITORY> extends DataInitializer {
  }

  interface DomainObjectIdentified<ROOT> extends AbstractView, Domain<ROOT>, Projectable<ROOT> {
  }

  interface JPQLQueryFactory {
  }

  interface GenericDeclaration extends AnnotatedElement {
    typeParameters: TypeVariable<any>[];
  }

  interface Type {
    typeName: string;
  }

  interface AnnotatedElement {
    annotations: Annotation[];
    declaredAnnotations: Annotation[];
  }

  interface QAuthoredObject {
    _super: QDatedObject;
    created: any;
    createdBy: QUserReference;
    createdByTenant: QTenantReference;
    deleted: any;
    deletedBy: QUserReference;
    deletedByTenant: QTenantReference;
    id: any;
    updated: any;
    updatedBy: QUserReference;
    updatedByTenant: QTenantReference;
    root: any;
    metadata: any;
    annotatedElement: AnnotatedElement;
    type: Class<T>;
  }

  interface QUserReference {
    id: any;
    name: any;
    root: any;
    metadata: any;
    annotatedElement: AnnotatedElement;
    type: Class<T>;
  }

  interface QTenantReference {
    id: any;
    name: any;
    root: any;
    metadata: any;
    annotatedElement: AnnotatedElement;
    type: Class<T>;
  }

  interface AuthoredStore<ROOT, PROJECTED, META_MODEL> extends DatedStore<ROOT, PROJECTED, META_MODEL> {
  }

  interface Authored<ROOT> extends Dated<ROOT> {
    createdByTenant: TenantReference;
    deletedByTenant: TenantReference;
    updatedBy: UserReference;
    createdBy: UserReference;
    deletedBy: UserReference;
    updatedByTenant: TenantReference;
  }

  interface DataInitializer {
  }

  interface TypeVariable<D> extends Type, AnnotatedElement {
    name: string;
    genericDeclaration: D;
    annotatedBounds: AnnotatedType[];
    bounds: Type[];
  }

  interface Annotation {
  }

  interface QDatedObject {
    _super: QDomainObject;
    created: any;
    deleted: any;
    id: any;
    updated: any;
    root: any;
    metadata: any;
    annotatedElement: AnnotatedElement;
    type: Class<T>;
  }

  interface AnnotatedType extends AnnotatedElement {
    type: Type;
    annotatedOwnerType: AnnotatedType;
  }

  interface QDomainObject {
    id: any;
    root: any;
    metadata: any;
    annotatedElement: AnnotatedElement;
    type: Class<T>;
  }

  interface DatedStore<ROOT, PROJECTED, META_MODEL> extends DomainStore<ROOT, PROJECTED, META_MODEL> {
  }

  interface DomainStore<ROOT, PROJECTED, META_MODEL> {
    queryFactory: JPAQueryFactory;
    type: Class<PROJECTED>;
    rootType: Class<ROOT>;
    metaModel: META_MODEL;
    entityManager: EntityManager;
    eventPublisher: ApplicationEventPublisher;
    differModuleAndProcessFields: DifferModule;
  }

  interface EntityManager {
    open: boolean;
    properties: { [index: string]: any };
    delegate: any;
    transaction: EntityTransaction;
    joinedToTransaction: boolean;
    entityManagerFactory: EntityManagerFactory;
    flushMode: FlushModeType;
    criteriaBuilder: CriteriaBuilder;
    metamodel: any;
  }

  interface ApplicationEventPublisher {
  }

  interface DifferModule {
    mappings: { [index: string]: string[] };
    eventNotifier: DifferEventNotifier;
    fieldsProcessor: DifferFieldsProcessor;
  }

  interface EntityTransaction {
    active: boolean;
    rollbackOnly: boolean;
  }

  interface EntityManagerFactory {
    open: boolean;
    properties: { [index: string]: any };
    cache: Cache;
    criteriaBuilder: CriteriaBuilder;
    metamodel: any;
    persistenceUnitUtil: PersistenceUnitUtil;
  }

  interface CriteriaBuilder {
  }

  interface DifferEventNotifier {
    eventPublisher: ApplicationEventPublisher;
  }

  interface DifferFieldsProcessor {
    applicationContext: ApplicationContext;
  }

  interface Cache {
  }

  interface PersistenceUnitUtil extends PersistenceUtil {
  }

  interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory, MessageSource, ApplicationEventPublisher, ResourcePatternResolver {
    parent: ApplicationContext;
    id: string;
    displayName: string;
    applicationName: string;
    startupDate: number;
    autowireCapableBeanFactory: AutowireCapableBeanFactory;
  }

  interface PersistenceUtil {
  }

  interface AutowireCapableBeanFactory extends BeanFactory {
  }

  interface Environment extends PropertyResolver {
    activeProfiles: string[];
    defaultProfiles: string[];
  }

  interface BeanFactory {
  }

  interface EnvironmentCapable {
    environment: Environment;
  }

  interface ListableBeanFactory extends BeanFactory {
    beanDefinitionCount: number;
    beanDefinitionNames: string[];
  }

  interface HierarchicalBeanFactory extends BeanFactory {
    parentBeanFactory: BeanFactory;
  }

  interface ResourcePatternResolver extends ResourceLoader {
  }

  interface PropertyResolver {
  }

  namespace eas {

    export type Language = "CZECH" | "ENGLISH" | "GERMAN" | "SLOVAK";

  }

  type ImageForDownload = "ILLUSTRATION" | "ILLUSTRATION_PAGE" | "FRONT_PAGE";

  type Radio = "ALBUM" | "STORYLINE" | "SLIDER";

  type NotificationType = "EMAIL";

  type NotificationEvent = "CONFIRM_REGISTRATION";

  type TargetProperty = "SUBJECT" | "CONTENT";

  type NotificationTemplateType = "EMAIL";

  type IconclassThemeState = "DONE" | "INPROGRESS" | "UNENRICHED";

  type LinkEnum = "ILLUSTRATION" | "ILLUSTRATION_PAGE" | "FRONT_PAGE" | "DIGITAL_COPY";

  type MarcRole = "BIBLIOGRAPHIC_ANTECEDENT" | "AUTHOR" | "PRESUMED_AUTHOR" | "PUBLISHER" | "CARTOGRAPHER" | "ENGRAVER" | "ETCHER" | "ILLUSTRATOR" | "METAL_ENGRAVER" | "PRINTER" | "WOODCARVER" | "OTHER";

  type Permission = "USER" | "ADMIN" | "SUPER_ADMIN";

  type EilRole = "USER" | "ADMIN" | "SUPER_ADMIN";

  type LogStrategy = "ALL" | "BRIEF" | "BRIEF_WITH_STACKTRACE" | "DETAILED" | "NONE";

  type MailState = "QUEUED" | "SENT" | "CANCELED" | "ERROR";

  type FlushModeType = "COMMIT" | "AUTO";

  type NotificationUnion = EmailNotification;

  type NotificationBaseUnion = EmailNotificationBase;

  type NotificationDetailUnion = EmailNotificationDetail;

  type NotificationListUnion = EmailNotificationList;

  type NotificationReceivedDetailUnion = EmailNotificationReceivedDetail;

  type NotificationReceivedListUnion = EmailNotificationReceivedList;

  type NotificationTemplateUnion = EmailNotificationTemplate;

  type NotificationTemplateCreateUnion = EmailNotificationTemplateCreate;

  type NotificationTemplateDetailUnion = EmailNotificationTemplateDetail;

  type NotificationTemplateIdentifiedUnion = EmailNotificationTemplateIdentified;

  type NotificationTemplateLabeledUnion = EmailNotificationTemplateLabeled;

  type NotificationTemplateListUnion = EmailNotificationTemplateList;

  type NotificationTemplateUpdateUnion = EmailNotificationTemplateUpdate;

  type RecordUnion = Illustration | Book;

  type RecordCreateUnion = IllustrationCreate | BookCreate;

  type RecordDetailUnion = IllustrationDetail | BookDetail;

  type RecordEssentialUnion = IllustrationEssential | BookEssential;

  type RecordExhibitionUnion = IllustrationExhibition | BookExhibition;

  type RecordFacetUnion = IllustrationFacet | BookFacet;

  type RecordIdentifiedUnion = IllustrationIdentified | BookIdentified;

  type RecordIdentifierUnion = IllustrationIdentifier | BookIdentifier;

  type RecordListUnion = IllustrationList | BookList;

  type RecordMarcUnion = IllustrationMarc | BookMarc;

  type RecordSourcesUnion = IllustrationSources | BookSources;

  type RecordUpdateUnion = IllustrationUpdate | BookUpdate;

  type RecordViseUnion = IllustrationVise | BookVise;

  type RecordXlsxUnion = IllustrationXlsx | BookXlsx;


  interface GrantedAuthority {
    authority: EilRole;
  }

  // Used in API /me
  interface Me {
    id: string;
    name: string;
    email: string;
    authorities: GrantedAuthority[];
    enabled: boolean;
    username: string;
    credentialsNonExpired: boolean;
    accountNonLocked: boolean;
    accountNonExpired: boolean;
  }

}

