export type ExploreItem = {
  id: string
  name: string
  image: string
  year: number
  selected: boolean
  author: string
}

export type Illustration = ExploreItem & {
  headline?: string
  year?: number
  description?: string
}

export type Metadata = {
  source?: string
  mainRecord?: string
  mainAuthor?: string
  publisherDetails?: string
  iconClass?: string[]
  extent?: string
  printer?: string
  placeOfPublication?: string
  notes?: string
  themes?: string[]
  personalName?: string
  genre?: string
  subjects?: string[]
  literature?: string[]
  links?: string[]
  catalogLink?: string
  exemplars?: string[]
  permanentLink?: string
}

export type IllustrationWithMetadata = Illustration & Metadata

export type Exhibition = {
  id: string
  name: string
  description: string
  coverImage: string
  illustrations: Illustration[] | null
}

export type ExhibitionView = 'ALBUM' | 'STORYLINE' | 'SLIDER'

export type View = 'TILES' | 'LIST'

export type RecordType = 'BOOK' | 'ILLUSTRATION'

export type LoginPhase =
  | 'LOGIN'
  | 'MENU'
  | 'REGISTRATION'
  | 'RECOVERY'
  | 'RECOVERED'
