import { create } from 'zustand'
import { TDropdown } from '../../../fe-shared/@types/common'

export interface TVariablesState {
  themes: string[]
  page: number
  illustrationsPerPage: number
  filterObject: TDropdown[]
  filterAuthor: TDropdown[]
  // filterPublishingPlace: TDropdown[]
}

interface TState extends TVariablesState {
  setThemes: (themes: string[]) => void
  setPage: (page: number) => void
  resetParams: () => void
  setFilterObject: (newValue: TDropdown[]) => void
  setFilterAuthor: (newValue: TDropdown[]) => void
  // setFilterPublishingPlace: (newValue: TDropdown[]) => void
}

export const useExploreStore = create<TState>()((set) => ({
  themes: [],
  page: 0,
  illustrationsPerPage: 20,
  filterObject: [],
  filterAuthor: [],
  // filterPublishingPlace: [],
  setThemes: (themes) => set(() => ({ themes })),
  setPage: (page) => set(() => ({ page })),
  resetParams: () =>
    set(() => ({
      themes: [],
      filterObject: [],
      filterPublishingPlace: [],
      page: 0,
    })),
  setFilterObject: (newValue) =>
    set(() => ({ filterObject: newValue, page: 0 })),
  setFilterAuthor: (newValue) =>
    set(() => ({ filterAuthor: newValue, page: 0 })),
  // setFilterPublishingPlace: (newValue) =>
  //   set(() => ({ filterPublishingPlace: newValue, page: 0 })),
}))
