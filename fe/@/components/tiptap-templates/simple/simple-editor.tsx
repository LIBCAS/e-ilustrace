'use client'

import { useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
  EditorContent,
  EditorContext,
  useEditor,
  useEditorState,
} from '@tiptap/react'

// --- Tiptap Core Extensions ---
import { StarterKit } from '@tiptap/starter-kit'
import { TaskItem, TaskList } from '@tiptap/extension-list'
import { TextAlign } from '@tiptap/extension-text-align'
import { Typography } from '@tiptap/extension-typography'
import { Highlight } from '@tiptap/extension-highlight'
import { Subscript } from '@tiptap/extension-subscript'
import { Superscript } from '@tiptap/extension-superscript'
import { FindAndReplace } from '@tiptap/extension-find-and-replace'
import { Color, TextStyle } from '@tiptap/extension-text-style'
import { CharacterCount, Selection } from '@tiptap/extensions'

// --- UI Primitives ---
import { Button } from '@/components/tiptap-ui-primitive/button'
import { Spacer } from '@/components/tiptap-ui-primitive/spacer'
import {
  Toolbar,
  ToolbarGroup,
  ToolbarSeparator,
} from '@/components/tiptap-ui-primitive/toolbar'

// --- Tiptap Node ---
import { HorizontalRule } from '@/components/tiptap-node/horizontal-rule-node/horizontal-rule-node-extension'
import '@/components/tiptap-node/blockquote-node/blockquote-node.scss'
import '@/components/tiptap-node/code-block-node/code-block-node.scss'
import '@/components/tiptap-node/horizontal-rule-node/horizontal-rule-node.scss'
import '@/components/tiptap-node/list-node/list-node.scss'
import '@/components/tiptap-node/image-node/image-node.scss'
import '@/components/tiptap-node/heading-node/heading-node.scss'
import '@/components/tiptap-node/paragraph-node/paragraph-node.scss'

// --- Tiptap UI ---
import { HeadingDropdownMenu } from '@/components/tiptap-ui/heading-dropdown-menu'
import { ListDropdownMenu } from '@/components/tiptap-ui/list-dropdown-menu'
import { BlockquoteButton } from '@/components/tiptap-ui/blockquote-button'
import { CodeBlockButton } from '@/components/tiptap-ui/code-block-button'
import {
  ColorHighlightPopover,
  ColorHighlightPopoverContent,
  ColorHighlightPopoverButton,
} from '@/components/tiptap-ui/color-highlight-popover'
import {
  LinkPopover,
  LinkContent,
  LinkButton,
} from '@/components/tiptap-ui/link-popover'
import { MarkButton } from '@/components/tiptap-ui/mark-button'
import { TextAlignButton } from '@/components/tiptap-ui/text-align-button'
import { UndoRedoButton } from '@/components/tiptap-ui/undo-redo-button'
import {
  SearchAndReplace,
  SearchAndReplaceButton,
} from '@/components/tiptap-ui/search-and-replace'
import {
  TextColorIcon,
  TextColorPopover,
  TextColorPopoverButton,
  TextColorPopoverContent,
} from '@/components/tiptap-ui/text-color-popover'

// --- Icons ---
import { ArrowLeftIcon } from '@/components/tiptap-icons/arrow-left-icon'
import { HighlighterIcon } from '@/components/tiptap-icons/highlighter-icon'
import { LinkIcon } from '@/components/tiptap-icons/link-icon'

// --- Hooks ---
import { useIsBreakpoint } from '@/hooks/use-is-breakpoint'

// --- Styles ---
import '@/components/tiptap-templates/simple/simple-editor.scss'

type SimpleEditorProps = {
  value: string
  onChange: (value: string) => void
  maxLength?: number
}

const SEARCH_AND_REPLACE_SCROLL_OPTIONS: ScrollIntoViewOptions = {
  block: 'center',
}

const MainToolbarContent = ({
  onHighlighterClick,
  onTextColorClick,
  onLinkClick,
  onSearchAndReplaceClick,
  isSearchAndReplaceOpen,
  searchAndReplaceButtonRef,
  isMobile,
}: {
  onHighlighterClick: () => void
  onTextColorClick: () => void
  onLinkClick: () => void
  onSearchAndReplaceClick: () => void
  isSearchAndReplaceOpen: boolean
  searchAndReplaceButtonRef: React.RefObject<HTMLButtonElement | null>
  isMobile: boolean
}) => {
  const { t } = useTranslation('editor')

  return (
    <>
      <Spacer />

      <ToolbarGroup>
        <UndoRedoButton
          action="undo"
          aria-label={t('undo')}
          tooltip={t('undo')}
        />
        <UndoRedoButton
          action="redo"
          aria-label={t('redo')}
          tooltip={t('redo')}
        />
      </ToolbarGroup>

      <ToolbarSeparator />

      <ToolbarGroup>
        <HeadingDropdownMenu modal={false} levels={[1, 2, 3, 4]} />
        <ListDropdownMenu
          modal={false}
          types={['bulletList', 'orderedList', 'taskList']}
        />
        <BlockquoteButton
          aria-label={t('blockquote')}
          tooltip={t('blockquote')}
        />
        <CodeBlockButton
          aria-label={t('code_block')}
          tooltip={t('code_block')}
        />
      </ToolbarGroup>

      <ToolbarSeparator />

      <ToolbarGroup>
        <MarkButton type="bold" aria-label={t('bold')} tooltip={t('bold')} />
        <MarkButton
          type="italic"
          aria-label={t('italic')}
          tooltip={t('italic')}
        />
        <MarkButton
          type="strike"
          aria-label={t('strike')}
          tooltip={t('strike')}
        />
        <MarkButton type="code" aria-label={t('code')} tooltip={t('code')} />
        <MarkButton
          type="underline"
          aria-label={t('underline')}
          tooltip={t('underline')}
        />
        {!isMobile ? (
          <TextColorPopover />
        ) : (
          <TextColorPopoverButton onClick={onTextColorClick} />
        )}
        {!isMobile ? (
          <ColorHighlightPopover
            aria-label={t('highlight')}
            tooltip={t('highlight')}
          />
        ) : (
          <ColorHighlightPopoverButton
            aria-label={t('highlight_text')}
            tooltip={t('highlight')}
            onClick={onHighlighterClick}
          />
        )}
        {!isMobile ? <LinkPopover /> : <LinkButton onClick={onLinkClick} />}
      </ToolbarGroup>

      <ToolbarSeparator />

      <ToolbarGroup>
        <MarkButton
          type="superscript"
          aria-label={t('superscript')}
          tooltip={t('superscript')}
        />
        <MarkButton
          type="subscript"
          aria-label={t('subscript')}
          tooltip={t('subscript')}
        />
      </ToolbarGroup>

      <ToolbarSeparator />

      <ToolbarGroup>
        <TextAlignButton
          align="left"
          aria-label={t('align_left')}
          tooltip={t('align_left')}
        />
        <TextAlignButton
          align="center"
          aria-label={t('align_center')}
          tooltip={t('align_center')}
        />
        <TextAlignButton
          align="right"
          aria-label={t('align_right')}
          tooltip={t('align_right')}
        />
        <TextAlignButton
          align="justify"
          aria-label={t('align_justify')}
          tooltip={t('align_justify')}
        />
      </ToolbarGroup>

      <Spacer />

      {isMobile && <ToolbarSeparator />}

      <ToolbarGroup>
        <SearchAndReplaceButton
          ref={searchAndReplaceButtonRef}
          aria-expanded={isSearchAndReplaceOpen}
          data-active-state={isSearchAndReplaceOpen ? 'on' : 'off'}
          onClick={onSearchAndReplaceClick}
        />
      </ToolbarGroup>
    </>
  )
}

const MobileToolbarContent = ({
  type,
  onBack,
}: {
  type: 'highlighter' | 'textColor' | 'link'
  onBack: () => void
}) => {
  const { t } = useTranslation('editor')

  return (
    <>
      <ToolbarGroup>
        <Button
          variant="ghost"
          aria-label={t('back')}
          tooltip={t('back')}
          onClick={onBack}
        >
          <ArrowLeftIcon className="tiptap-button-icon" />
          {type === 'highlighter' ? (
            <HighlighterIcon className="tiptap-button-icon" />
          ) : type === 'textColor' ? (
            <TextColorIcon />
          ) : (
            <LinkIcon className="tiptap-button-icon" />
          )}
        </Button>
      </ToolbarGroup>

      <ToolbarSeparator />

      {type === 'highlighter' ? (
        <ColorHighlightPopoverContent />
      ) : type === 'textColor' ? (
        <TextColorPopoverContent />
      ) : (
        <LinkContent />
      )}
    </>
  )
}

export function SimpleEditor({
  value,
  onChange,
  maxLength = 750,
}: SimpleEditorProps) {
  const { t } = useTranslation('editor')
  const isMobile = useIsBreakpoint()
  const [mobileView, setMobileView] = useState<
    'main' | 'highlighter' | 'textColor' | 'link'
  >('main')
  const [isSearchAndReplaceOpen, setIsSearchAndReplaceOpen] = useState(false)
  const toolbarRef = useRef<HTMLDivElement>(null)
  const searchAndReplaceButtonRef = useRef<HTMLButtonElement>(null)

  const editor = useEditor({
    immediatelyRender: false,
    editorProps: {
      attributes: {
        autocomplete: 'off',
        autocorrect: 'off',
        autocapitalize: 'off',
        'aria-label': t('content_area'),
        class: 'simple-editor',
      },
    },
    extensions: [
      StarterKit.configure({
        horizontalRule: false,
        link: {
          openOnClick: false,
          enableClickSelection: true,
        },
      }),
      HorizontalRule,
      TextAlign.configure({ types: ['heading', 'paragraph'] }),
      TaskList,
      TaskItem.configure({ nested: true }),
      Highlight.configure({ multicolor: true }),
      TextStyle,
      Color,
      Typography,
      Superscript,
      Subscript,
      Selection,
      CharacterCount.configure({ limit: maxLength }),
      FindAndReplace.configure({
        searchDebounceMs: 500,
        injectCSS: false,
      }),
    ],
    content: value,
    onUpdate: ({ editor: updatedEditor }) => {
      onChange(updatedEditor.getHTML())
    },
  })
  const characterCount = useEditorState({
    editor,
    selector: ({ editor: currentEditor }) =>
      currentEditor?.storage.characterCount.characters() || 0,
  })

  useEffect(() => {
    if (!editor || editor.getHTML() === value) {
      return
    }

    editor.commands.setContent(value, { emitUpdate: false })
  }, [editor, value])

  useEffect(() => {
    if (!isMobile && mobileView !== 'main') {
      setMobileView('main')
    }
  }, [isMobile, mobileView])

  const openSearchAndReplace = useCallback(() => {
    setMobileView('main')
    setIsSearchAndReplaceOpen(true)
  }, [])

  const closeSearchAndReplace = useCallback(() => {
    setIsSearchAndReplaceOpen(false)
    searchAndReplaceButtonRef.current?.focus()
  }, [])

  const toggleSearchAndReplace = useCallback(() => {
    if (isSearchAndReplaceOpen) {
      closeSearchAndReplace()
      return
    }

    openSearchAndReplace()
  }, [closeSearchAndReplace, isSearchAndReplaceOpen, openSearchAndReplace])

  return (
    <div className="simple-editor-field">
      <div className="simple-editor-wrapper">
        <EditorContext.Provider value={{ editor }}>
          <Toolbar ref={toolbarRef} aria-label={t('toolbar')}>
            {mobileView === 'main' ? (
              <MainToolbarContent
                onHighlighterClick={() => setMobileView('highlighter')}
                onTextColorClick={() => setMobileView('textColor')}
                onLinkClick={() => setMobileView('link')}
                onSearchAndReplaceClick={toggleSearchAndReplace}
                isSearchAndReplaceOpen={isSearchAndReplaceOpen}
                searchAndReplaceButtonRef={searchAndReplaceButtonRef}
                isMobile={isMobile}
              />
            ) : (
              <MobileToolbarContent
                type={mobileView}
                onBack={() => setMobileView('main')}
              />
            )}
          </Toolbar>

          <SearchAndReplace
            className="simple-editor-search-and-replace"
            open={isSearchAndReplaceOpen}
            onOpen={openSearchAndReplace}
            onClose={closeSearchAndReplace}
            scrollIntoViewOptions={SEARCH_AND_REPLACE_SCROLL_OPTIONS}
          />

          <EditorContent
            editor={editor}
            role="presentation"
            className="simple-editor-content"
          />
        </EditorContext.Provider>
      </div>
      <span className="simple-editor-character-count">
        {characterCount}/{maxLength}
      </span>
    </div>
  )
}
