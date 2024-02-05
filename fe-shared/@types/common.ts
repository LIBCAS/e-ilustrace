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