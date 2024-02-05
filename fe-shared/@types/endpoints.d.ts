/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.34.976 on 2023-06-21 12:32:43.

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

  interface AuthorList extends DatedObjectDefault<Author>, View {
    fullName: string;
    authorityCode: string;
  }

  interface AuthorRef {
    id: string;
  }

  interface AuthorUpdate extends DatedObjectUpdate<Author>, View {
  }

  interface RecordAuthor extends DomainObject<RecordAuthor> {
    author: Author;
    roles: MarcRole[];
    mainAuthor: boolean;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface RecordAuthorDetail extends DomainObjectDefault<RecordAuthor>, View {
    author: AuthorDetail;
    roles: MarcRole[];
    mainAuthor: boolean;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface RecordAuthorList extends DomainObjectDefault<RecordAuthor>, View {
    author: AuthorList;
    roles: MarcRole[];
    mainAuthor: boolean;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface Authority {
    authorityCode: string;
  }

  interface DownloadReference extends DomainObject<DownloadReference> {
    created: Date;
    url: string;
    referencedAttribute: ImageForDownload;
    record: RecordUnion;
    downloaded: boolean;
    failed: boolean;
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

  interface Person {
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

  interface PublishingPlace extends DatedObject<PublishingPlace>, Authority {
    name: string;
    country: string;
    authorityCode: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface PublishingPlaceCreate extends DatedObjectCreate<PublishingPlace>, View {
  }

  interface PublishingPlaceDetail extends DatedObjectDefault<PublishingPlace>, View, Authority {
    name: string;
    country: string;
    authorityCode: string;
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
    coauthors: RecordAuthor[];
    mainWorkshop: RecordInstitution;
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
    mainAuthor: RecordAuthorDefault | null;
    coauthors: RecordAuthorDefault[];
    mainWorkshop: RecordInstitutionDefault;
    coinstitutions: RecordInstitutionDefault[];
  }

  interface RecordIdentified extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    fixedLengthField: string;
    title: string;
  }

  interface RecordList extends DatedObjectDefault<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    identifier: string;
    yearFrom: number;
    yearTo: number;
    title: string;
    mainAuthor: RecordAuthorDefault;
    mainWorkshop: RecordInstitutionDefault;
  }

  interface RecordUpdate extends DatedObjectUpdate<Record>, AbstractView {
    type: "BOOK" | "ILLUSTRATION";
    iconclass: IconclassCategoryRef[];
    themes: ThemeRef[];
  }

  interface RecordTypeDto {
    type: string;
  }

  interface RecordYearsDto {
    yearFrom: number;
    yearTo: number;
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
        authors: RecordAuthorDefault[]
        title: string;
        mainAuthor: RecordAuthorDefault;
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

  interface BookIdentified extends RecordIdentified, View {
    type: "BOOK";
    illustrations: IllustrationIdentified[];
  }

  interface BookList extends RecordList, View {
    type: "BOOK";
    illustrations: IllustrationIdentified[];
    publishingEntry: PublicationEntryDefault;
    frontPageScan: FileRef | null;
  }

  interface BookRef {
    id: string;
  }

  interface BookUpdate extends RecordUpdate, View {
    type: "BOOK";
  }

  interface BookEssential extends RecordEssential, View {
    type: "BOOK";
    frontPageScan: FileDetail;
  }

  interface BookExhibition extends RecordExhibition, View {
    type: "BOOK";
  }

  interface Illustration extends Record {
    type: "ILLUSTRATION";
    book: Book;
    printEntry: PublicationEntry;
    printingPlateEntry: PublicationEntry;
    defect: string;
    illustrationScan: File;
    pageScan: File;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
  }

  interface IllustrationCreate extends RecordCreate, View {
    type: "ILLUSTRATION";
  }

  interface IllustrationDetail extends RecordDetail, View {
    type: "ILLUSTRATION";
    book: BookIdentified | null;
    printEntry: PublicationEntryDefault;
    printingPlateEntry: PublicationEntryDefault | null;
    defect: string | null;
    illustrationScan: FileDetail | null;
    pageScan: FileDetail | null;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
    viseFileId: String;
  }

  interface IllustrationIdentified extends RecordIdentified, View {
    type: "ILLUSTRATION";
    book: BookRef;
  }

  interface IllustrationList extends RecordList, View {
    type: "ILLUSTRATION";
    printEntry: PublicationEntryDefault;
    illustrationScan: FileRef | null;
    pageScan: FileRef | null;
    iconclassState: IconclassThemeState;
    themeState: IconclassThemeState;
    viseFileId: String;
  }

  interface IllustrationRef {
    id: string;
  }

  interface IllustrationUpdate extends RecordUpdate, View {
    type: "ILLUSTRATION";
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
      book: BookExhibition;
      illustrationScan: FileRef;
      pageScan: FileRef;
        type: "ILLUSTRATION";
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
    messageSource: MessageSource;
  }

  interface PasswordEncoderFactory {
  }

  interface Token extends DomainObject<Token> {
    user: User;
    expiration: Date;
    used: boolean;
  }

  interface SubjectEntry extends DatedObject<SubjectEntry>, Authority {
    label: string;
    authorityCode: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectEntryCreate extends DatedObjectCreate<SubjectEntry>, View {
  }

  interface SubjectEntryDetail extends DatedObjectDefault<SubjectEntry>, View, Authority {
    label: string;
    authorityCode: string;
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
    authorityCode: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPersonCreate extends DatedObjectCreate<SubjectPerson>, View {
  }

  interface SubjectPersonDetail extends DatedObjectDefault<SubjectPerson>, View, Person, Authority {
    fullName: string;
    birthYear: string;
    deathYear: string;
    authorityCode: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPersonList extends DatedObjectDefault<SubjectPerson>, View, Authority {
    fullName: string;
    authorityCode: string;
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
    authorityCode: string;
    fromBook: boolean;
    fromIllustration: boolean;
  }

  interface SubjectPlaceCreate extends DatedObjectCreate<SubjectPlace>, View {
  }

  interface SubjectPlaceDetail extends DatedObjectDefault<SubjectPlace>, View, Authority {
    name: string;
    authorityCode: string;
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

  interface UserDetail extends DatedObjectDefault<User>, View {
    email: string;
    firstName: string;
    lastName: string;
    role: EilRole;
    validated: boolean;
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
    role: EilRole;
  }

  interface UserEssential extends DatedObjectDefault<User>, View {
    firstName: string;
    lastName: string;
    fullName: string;
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
    bookToIllustration: Illustration;
  }

  interface Constants {
  }

  interface Utils {
  }

  interface DataFieldMapper {
    illustrationMapper: boolean;
    bookMapper: boolean;
    tag: string;
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

  interface DataFieldMapper998 extends DataFieldMapper {
  }

  interface DataFieldMapper264 {
    indicator: string;
  }

  interface DataFieldMapper2643 extends DataFieldMapper264, DataFieldMapper {
  }

  interface DataFieldMapper264X extends DataFieldMapper264, DataFieldMapper {
  }

  interface ExhibitionItem extends DatedObject<ExhibitionItem> {
    description: string;
    illustration: Illustration;
    exhibition: Exhibition;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemCreate extends DatedObjectCreate<ExhibitionItem> {
    description: string;
    illustration: IllustrationRef;
    exhibition: ExhibitionRef;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemDetail extends DatedObjectDefault<ExhibitionItem> {
    description: string;
    illustration: IllustrationExhibition;
    exhibition: ExhibitionRef;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemList extends DatedObjectDefault<ExhibitionItem> {
    description: string;
    name: string;
    year: string;
    preface: boolean;
  }

  interface ExhibitionItemRef {
    id: string;
  }

  interface ExhibitionItemExternalUpdate extends DatedObjectDefault<ExhibitionItem> {
    description: string;
    illustration: IllustrationRef;
    exhibition: ExhibitionRef;
    name: string;
    year: string;
    preface: boolean;
  }

  interface Exhibition extends DatedObject<Exhibition> {
    name: string;
    description: string;
    published: Date;
    items: ExhibitionItem[];
    user: User;
    radio: Radio;
  }

  interface ExhibitionCreate extends DatedObjectCreate<Exhibition> {
    name: string;
    description: string;
    items: ExhibitionItemCreate[];
    user: UserRef;
    radio: Radio;
  }

  interface ExhibitionDetail extends DatedObjectDefault<Exhibition> {
    name: string;
    description: string;
    published: Date;
    items: ExhibitionItemDetail[];
    user: UserEssential;
    radio: Radio;
    itemsCount: number;
  }

  interface ExhibitionList extends DatedObjectDefault<Exhibition> {
    name: string;
    description: string;
    published: Date;
    items: ExhibitionItemDetail[];
    user: UserEssential;
    radio: Radio;
    itemsCount: number;
  }

  interface ExhibitionUpdate extends DatedObjectDefault<Exhibition> {
    name: string;
    description: string;
    items: ExhibitionItemExternalUpdate[];
    radio: Radio;
  }

  interface ExhibitionRef {
    id: string;
  }

  interface Selection extends DatedObject<Selection> {
    items: SelectionItem[];
    user: User
  }

  interface SelectionDetail extends DatedObjectDefault<Selection>, View {
    items: SelectionItemDetail[];
    user: UserRef
  }

  interface SelectionUpdate extends DatedObjectUpdate<Selection>, View {
    items: SelectionItemExternalUpdate[];
  }

  interface SelectionUpdateDto {
    illustrations: IllustrationRef[];
    books: BookRef[];
  }

  interface SelectionDto {
    items: SelectionItemRef[];
  }

  interface SelectionItem extends DomainObject<SelectionItem> {
    illustration: Illustration;
    book: Book;
    selection: Selection;
    mirador: boolean;
  }

  interface SelectionRef {
    id: string;
  }

  interface SelectionItemRef {
    id: string;
  }

  interface SelectionItemDetail extends DomainObjectDefault<SelectionItem>, View {
    illustration?: IllustrationEssential;
    book?: BookEssential;
    selection: SelectionRef;
    mirador: boolean;
  }

  interface SelectionItemExternalUpdate extends DomainObjectDefault<SelectionItem>, View {
    illustration: IllustrationRef;
    book: BookRef;
    selection: SelectionRef;
    mirador: boolean;
  }

  interface Keyword extends DomainObject<Keyword> {
    label: string;
  }

  interface KeywordDetail extends DomainObjectDefault<Keyword>, View {
    label: string;
  }

  interface KeywordList extends DomainObjectDefault<Keyword>, View {
    label: string;
  }

  interface KeywordCreate extends DomainObjectCreate<Keyword>, View {
    label: string;
  }

  interface KeywordUpdate extends DomainObjectUpdate<Keyword>, View {
    label: string;
  }

  interface View {
  }

  interface AbstractView {
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

  interface DomainObjectDefault<ROOT> extends AbstractView, Domain<ROOT>, Projectable<ROOT> {
  }

  interface DatedInitializer<ROOT, REPOSITORY> extends DomainInitializer<ROOT, REPOSITORY> {
  }

  // Used in API /me
  interface User {
    id: string;
    name: string;
    email: string;
    authorities: GrantedAuthority[];
    tenant: Tenant;
    enabled: boolean;
  }

  interface UserReference {
    id: string;
    name: string;
  }

  interface TenantReference {
    id: string;
    name: string;
  }

  interface Tenant {
    id: string;
    name: string;
  }

  interface GrantedAuthority {
    authority: string;
  }

  interface AuthoredObject<ROOT> extends DatedObject<ROOT>, Authored<ROOT> {
  }

  interface AuthoredObjectDefault<ROOT> extends DatedObjectDefault<ROOT>, AbstractView, Authored<ROOT> {
  }

  interface Dated<ROOT> extends Domain<ROOT> {
    created: Date;
    updated: Date;
    deleted: Date;
  }

  interface DomainObjectCreate<ROOT> extends AbstractView, Projectable<ROOT> {
  }

  interface DomainObjectUpdate<ROOT> extends AbstractView, Projectable<ROOT> {
  }

  interface Domain<ROOT> extends Projectable<ROOT> {
    id: string;
  }

  interface Projectable<ROOT> {
  }

  interface DomainInitializer<ROOT, REPOSITORY> extends DataInitializer {
  }

  interface DataInitializer {
  }

  interface Authored<ROOT> extends Dated<ROOT> {
    createdBy: UserReference;
    createdByTenant: TenantReference;
    updatedBy: UserReference;
    updatedByTenant: TenantReference;
    deletedBy: UserReference;
    deletedByTenant: TenantReference;
  }

  namespace eas {

    export type Language = "CZECH" | "ENGLISH" | "GERMAN" | "SLOVAK";

  }

  type ImageForDownload = "ILLUSTRATION" | "ILLUSTRATION_PAGE" | "FRONT_PAGE";

  type MarcRole = "BIBLIOGRAPHIC_ANTECEDENT" | "AUTHOR" | "PRESUMED_AUTHOR" | "PUBLISHER" | "CARTOGRAPHER" | "ENGRAVER" | "ETCHER" | "ILLUSTRATOR" | "METAL_ENGRAVER" | "PRINTER" | "WOODCARVER" | "OTHER";

  type EilRole = "USER" | "ADMIN" | "SUPER_ADMIN";

  type RecordUnion = Illustration | Book;

  type RecordCreateUnion = IllustrationCreate | BookCreate;

  type RecordDetailUnion = IllustrationDetail | BookDetail;

  type RecordIdentifiedUnion = IllustrationIdentified | BookIdentified;

  type RecordListUnion = IllustrationList | BookList;

  type RecordUpdateUnion = IllustrationUpdate | BookUpdate;

  type IconclassThemeState = "DONE" | "INPROGRESS" | "UNENRICHED";

  type Radio = "ALBUM" | "STORYLINE" | "SLIDER";
}
