import { NavLink } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  GraduationCap,
  Users,
  Scale,
  Shuffle,
  History,
  ChevronLeft,
  ChevronRight,
  Moon,
  Sun,
} from 'lucide-react'
import { cn } from '@/lib/utils'

const NAV_ITEMS = [
  { path: '/students', icon: Users, label: 'nav.students' },
  { path: '/compatibility', icon: Scale, label: 'nav.compatibility' },
  { path: '/generate', icon: Shuffle, label: 'nav.generateGroups' },
  { path: '/history', icon: History, label: 'nav.history' },
] as const

interface SidebarProps {
  collapsed: boolean
  onToggleCollapse: () => void
  theme: 'light' | 'dark'
  onToggleTheme: () => void
}

const itemBase =
  'flex items-center gap-3 rounded-md text-sm font-medium transition-colors w-full'
const itemInactive =
  'text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground'
const itemActive = 'bg-sidebar-primary text-sidebar-primary-foreground'

export default function Sidebar({
  collapsed,
  onToggleCollapse,
  theme,
  onToggleTheme,
}: SidebarProps) {
  const { t } = useTranslation()

  const px = collapsed ? 'justify-center px-0 h-9 w-9 mx-auto' : 'px-3 py-2'

  return (
    <aside
      className={cn(
        'hidden md:flex flex-col border-r border-border bg-sidebar overflow-hidden transition-[width] duration-300 ease-in-out shrink-0',
        collapsed ? 'w-[60px]' : 'w-64'
      )}
    >
      <div
        className={cn(
          'flex items-center h-14 border-b border-border shrink-0',
          collapsed ? 'justify-center' : 'gap-2.5 px-4'
        )}
      >
        <GraduationCap className="h-5 w-5 text-primary shrink-0" />
        {!collapsed && (
          <span className="font-semibold text-sm text-sidebar-foreground truncate">
            {t('app.title')}
          </span>
        )}
      </div>

      <nav className="flex-1 p-2 space-y-0.5 overflow-y-auto">
        {NAV_ITEMS.map(({ path, icon: Icon, label }) => (
          <NavLink
            key={path}
            to={path}
            title={collapsed ? t(label) : undefined}
            className={({ isActive }) =>
              cn(itemBase, px, isActive ? itemActive : itemInactive)
            }
          >
            <Icon className="h-4 w-4 shrink-0" />
            {!collapsed && <span>{t(label)}</span>}
          </NavLink>
        ))}
      </nav>

      <div className="p-2 border-t border-border space-y-0.5 shrink-0">
        <button
          onClick={onToggleTheme}
          title={t(theme === 'dark' ? 'theme.light' : 'theme.dark')}
          className={cn(itemBase, px, itemInactive)}
        >
          {theme === 'dark' ? (
            <Sun className="h-4 w-4 shrink-0" />
          ) : (
            <Moon className="h-4 w-4 shrink-0" />
          )}
          {!collapsed && (
            <span>{t(theme === 'dark' ? 'theme.light' : 'theme.dark')}</span>
          )}
        </button>

        <button
          onClick={onToggleCollapse}
          title={t(collapsed ? 'sidebar.expand' : 'sidebar.collapse')}
          className={cn(itemBase, px, itemInactive)}
        >
          {collapsed ? (
            <ChevronRight className="h-4 w-4 shrink-0" />
          ) : (
            <ChevronLeft className="h-4 w-4 shrink-0" />
          )}
          {!collapsed && <span>{t('sidebar.collapse')}</span>}
        </button>
      </div>
    </aside>
  )
}
