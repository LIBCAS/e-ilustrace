import { create } from 'zustand'

export interface TIllustration {
  itemId?: string
  id: string
  description: string
  name: string
  year: string
  preface: boolean
}

interface TVariablesState {
  name: string
  description: string
  radio: 'ALBUM' | 'STORYLINE' | 'SLIDER'
  items: TIllustration[]
  illustrationForDeletion: string[]
}

interface TState extends TVariablesState {
  setName: (name: string) => void
  setDescription: (description: string) => void
  setRadio: (radio: 'ALBUM' | 'STORYLINE' | 'SLIDER') => void
  setItems: (illustrations: TIllustration[]) => void
  setInitialState: () => void
  setIllustrationForDeletion: (illustrationForDeletion: string[]) => void
}

export const useNewExhibitionStore = create<TState>()((set) => ({
  name: '',
  description: '',
  radio: 'ALBUM',
  items: [],
  illustrationForDeletion: [],
  setName: (name) => set(() => ({ name })),
  setDescription: (description) => set(() => ({ description })),
  setRadio: (radio) => set(() => ({ radio })),
  setItems: (items) => set(() => ({ items })),
  setInitialState: () =>
    set(() => ({
      name: '',
      description: '',
      radio: 'ALBUM',
      items: [],
      illustrationForDeletion: [],
    })),
  setIllustrationForDeletion: (illustrationForDeletion) =>
    set(() => ({ illustrationForDeletion })),
}))
