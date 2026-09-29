import type { Backend } from "./endpoints";

export type TRecordYears = Backend.RecordYearsDto;

export type TFilterOperator = "EQ" | "CONTAINS" | "FTX" | "FTXF" | "GTE" | "LTE" | "OR" | "AND" | "START_WITH"

export type TDropdownWithOperator = {
  value: string
  label: string
  operation: TFilterOperator
}

export type TDropdown = {
  value: string
  label: string
}

export type TSortTypes =
  | 'title_ASC'
  | 'title_DESC'
  | 'yearFrom_ASC'
  | 'yearFrom_DESC'

export type ExhibitionView = 'ALBUM' | 'STORYLINE' | 'SLIDER'

export type View = 'TILES' | 'LIST'

export type RecordType = 'BOOK' | 'ILLUSTRATION'

export type LoginPhase =
  | 'LOGIN'
  | 'MENU'
  | 'REGISTRATION'
  | 'RECOVERY'
  | 'RECOVERED'
  | 'PASSWORD_CHANGE'

export type TFilter = {
  field?: string
  operation: string
  value?: string | number
  filters?: TFilter[]
}
