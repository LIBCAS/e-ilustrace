import { create } from 'zustand'
import { LoginPhase } from '../../../fe-shared/@types/common'

export interface TVariablesState {
  sidebarOpen: boolean
  loginPhase: LoginPhase
}

interface TState extends TVariablesState {
  setSidebarOpen: (opened: boolean) => void
  setLoginPhase: (loginPhase: LoginPhase) => void
}

export const useSidebarStore = create<TState>()((set) => ({
  sidebarOpen: false,
  loginPhase: 'MENU',
  setSidebarOpen: (opened) => set(() => ({ sidebarOpen: opened })),
  setLoginPhase: (loginPhase) => set(() => ({ loginPhase })),
}))
