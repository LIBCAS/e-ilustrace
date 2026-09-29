import { forwardRef, useCallback, useState, type ForwardedRef } from 'react'
import { type Editor } from '@tiptap/react'
import { useTranslation } from 'react-i18next'

// --- Hooks ---
import { useTiptapEditor } from '@/hooks/use-tiptap-editor'

// --- Icons ---
import { ChevronDownIcon } from '@/components/tiptap-icons/chevron-down-icon'

// --- Tiptap UI ---
import { ListButton, type ListType } from '@/components/tiptap-ui/list-button'

import { useListDropdownMenu } from '@/components/tiptap-ui/list-dropdown-menu/use-list-dropdown-menu'

// --- UI Primitives ---
import type { ButtonProps } from '@/components/tiptap-ui-primitive/button'
import { Button } from '@/components/tiptap-ui-primitive/button'
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuGroup,
} from '@/components/tiptap-ui-primitive/dropdown-menu'

export interface ListDropdownMenuProps extends Omit<ButtonProps, 'type'> {
  /**
   * The Tiptap editor instance.
   */
  editor?: Editor
  /**
   * The list types to display in the dropdown.
   */
  types?: ListType[]
  /**
   * Whether the dropdown should be hidden when no list types are available
   * @default false
   */
  hideWhenUnavailable?: boolean
  /**
   * Callback for when the dropdown opens or closes
   */
  onOpenChange?: (isOpen: boolean) => void
  /**
   * Whether the dropdown should use a modal
   */
  modal?: boolean
}

function ListDropdownMenuImpl(
  {
    editor: providedEditor,
    types = ['bulletList', 'orderedList', 'taskList'],
    hideWhenUnavailable = false,
    onOpenChange,
    modal = true,
    ...props
  }: ListDropdownMenuProps,
  ref: ForwardedRef<HTMLButtonElement>
) {
  const { t } = useTranslation('editor')
  const { editor } = useTiptapEditor(providedEditor)
  const [isOpen, setIsOpen] = useState(false)

  const { filteredLists, canToggle, isActive, isVisible, Icon } =
    useListDropdownMenu({
      editor,
      types,
      hideWhenUnavailable,
    })

  const handleOnOpenChange = useCallback(
    (open: boolean) => {
      setIsOpen(open)
      onOpenChange?.(open)
    },
    [onOpenChange]
  )

  if (!isVisible) {
    return null
  }

  return (
    <DropdownMenu modal={modal} open={isOpen} onOpenChange={handleOnOpenChange}>
      <DropdownMenuTrigger asChild>
        <Button
          type="button"
          variant="ghost"
          data-active-state={isActive ? 'on' : 'off'}
          role="button"
          tabIndex={-1}
          disabled={!canToggle}
          data-disabled={!canToggle}
          aria-label={t('list_options')}
          tooltip={t('list')}
          {...props}
          ref={ref}
        >
          <Icon className="tiptap-button-icon" />
          <ChevronDownIcon className="tiptap-button-dropdown-small" />
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent align="start">
        <DropdownMenuGroup>
          {filteredLists.map((option) => {
            const label = t(
              option.type === 'bulletList'
                ? 'bullet_list'
                : option.type === 'orderedList'
                  ? 'ordered_list'
                  : 'task_list'
            )

            return (
              <DropdownMenuItem key={option.type} asChild>
                <ListButton
                  editor={editor}
                  type={option.type}
                  text={label}
                  aria-label={label}
                  tooltip={label}
                  showTooltip={false}
                />
              </DropdownMenuItem>
            )
          })}
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export const ListDropdownMenu = forwardRef(ListDropdownMenuImpl)

ListDropdownMenu.displayName = 'ListDropdownMenu'

export default ListDropdownMenu
