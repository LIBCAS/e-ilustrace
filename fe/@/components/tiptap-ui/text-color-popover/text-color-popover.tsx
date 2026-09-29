import { forwardRef, useState } from 'react'
import { useEditorState } from '@tiptap/react'
import { useTranslation } from 'react-i18next'

import { BanIcon } from '@/components/tiptap-icons/ban-icon'
import { useTiptapEditor } from '@/hooks/use-tiptap-editor'
import type { ButtonProps } from '@/components/tiptap-ui-primitive/button'
import { Button } from '@/components/tiptap-ui-primitive/button'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/tiptap-ui-primitive/popover'
import {
  Card,
  CardBody,
  CardItemGroup,
} from '@/components/tiptap-ui-primitive/card'
import { ButtonGroup } from '@/components/tiptap-ui-primitive/button-group'
import { Separator } from '@/components/tiptap-ui-primitive/separator'

import './text-color-popover.scss'

const TEXT_COLORS = [
  { value: '#1d1e20', labelKey: 'text_color_black' },
  { value: '#6b7280', labelKey: 'text_color_gray' },
  { value: '#e2293f', labelKey: 'text_color_red' },
  { value: '#2563eb', labelKey: 'text_color_blue' },
  { value: '#15803d', labelKey: 'text_color_green' },
  { value: '#ea580c', labelKey: 'text_color_orange' },
] as const

const normalizeColor = (color?: string | null) => {
  if (!color) return undefined

  const rgb = color.match(
    /^rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)(?:\s*,[^)]*)?\)$/i
  )
  if (!rgb) return color.toLowerCase()

  return `#${rgb
    .slice(1, 4)
    .map((channel) => Number(channel).toString(16).padStart(2, '0'))
    .join('')}`
}

export function TextColorIcon({ color }: { color?: string }) {
  return (
    <span
      aria-hidden="true"
      className="tiptap-text-color-icon"
      style={{ '--text-color': color || 'currentColor' } as React.CSSProperties}
    >
      A
    </span>
  )
}

export const TextColorPopoverButton = forwardRef<
  HTMLButtonElement,
  ButtonProps
>(({ children, ...props }, ref) => {
  const { t } = useTranslation('editor')
  const { editor } = useTiptapEditor()
  const color = useEditorState({
    editor,
    selector: ({ editor: currentEditor }) =>
      currentEditor?.getAttributes('textStyle').color as string | undefined,
  })
  return (
    <Button
      type="button"
      variant="ghost"
      aria-label={t('text_color')}
      tooltip={t('text_color')}
      ref={ref}
      {...props}
    >
      {children ?? <TextColorIcon color={color ?? undefined} />}
    </Button>
  )
})

TextColorPopoverButton.displayName = 'TextColorPopoverButton'

export function TextColorPopoverContent() {
  const { t } = useTranslation('editor')
  const { editor } = useTiptapEditor()
  const color = useEditorState({
    editor,
    selector: ({ editor: currentEditor }) =>
      currentEditor?.getAttributes('textStyle').color as string | undefined,
  })
  const normalizedColor = normalizeColor(color)

  const setColor = (nextColor: string) => {
    editor?.chain().focus().setColor(nextColor).run()
  }

  const setCustomColor = (nextColor: string) => {
    editor?.commands.setColor(nextColor)
  }

  const unsetColor = () => {
    editor?.chain().focus().unsetColor().run()
  }

  return (
    <Card>
      <CardBody>
        <CardItemGroup orientation="horizontal">
          <ButtonGroup>
            {TEXT_COLORS.map(({ value, labelKey }) => {
              const label = t(labelKey)

              return (
                <Button
                  type="button"
                  variant="ghost"
                  data-active-state={normalizedColor === value ? 'on' : 'off'}
                  aria-label={t('text_color_value', { color: label })}
                  tooltip={label}
                  onClick={() => setColor(value)}
                  key={value}
                >
                  <span
                    aria-hidden="true"
                    className="tiptap-text-color-swatch"
                    style={{ '--text-color': value } as React.CSSProperties}
                  />
                </Button>
              )
            })}
          </ButtonGroup>

          <ButtonGroup>
            <label
              className="tiptap-text-color-custom"
              aria-label={t('text_color_custom')}
              title={t('text_color_custom')}
            >
              <input
                type="color"
                value={normalizedColor || '#e2293f'}
                aria-label={t('text_color_custom')}
                onChange={(event) => setCustomColor(event.target.value)}
              />
            </label>
          </ButtonGroup>

          <Separator />

          <ButtonGroup>
            <Button
              type="button"
              variant="ghost"
              data-active-state={!color ? 'on' : 'off'}
              aria-label={t('text_color_default')}
              tooltip={t('text_color_default')}
              onClick={unsetColor}
            >
              <BanIcon className="tiptap-button-icon" />
            </Button>
          </ButtonGroup>
        </CardItemGroup>
      </CardBody>
    </Card>
  )
}

export function TextColorPopover() {
  const { t } = useTranslation('editor')
  const [isOpen, setIsOpen] = useState(false)

  return (
    <Popover open={isOpen} onOpenChange={setIsOpen}>
      <PopoverTrigger asChild>
        <TextColorPopoverButton />
      </PopoverTrigger>
      <PopoverContent
        aria-label={t('text_color_palette')}
        onFocusOutside={(event) => {
          if (
            document.activeElement instanceof HTMLInputElement &&
            document.activeElement.type === 'color'
          ) {
            event.preventDefault()
          }
        }}
      >
        <TextColorPopoverContent />
      </PopoverContent>
    </Popover>
  )
}
