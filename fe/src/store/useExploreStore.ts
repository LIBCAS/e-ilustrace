import { create } from 'zustand'

export interface TVariablesState {
  themes: string[]
  search: string
  page: number
  illustrationsPerPage: number
}

interface TState extends TVariablesState {
  setThemes: (themes: string[]) => void
  setSearch: (search: string) => void
  setPage: (page: number) => void
  resetParams: () => void
}

export const useExploreStore = create<TState>()((set) => ({
  themes: [],
  search: '',
  page: 0,
  illustrationsPerPage: 20,
  setThemes: (themes) => set(() => ({ themes })),
  setSearch: (search) => set(() => ({ search })),
  setPage: (page) => set(() => ({ page })),
  resetParams: () => set(() => ({ themes: [], search: '', page: 0 })),
}))
