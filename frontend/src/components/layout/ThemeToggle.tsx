import { Moon, Sun } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface ThemeToggleProps {
  theme: 'light' | 'dark'
  onToggle: () => void
}

export default function ThemeToggle({ theme, onToggle }: ThemeToggleProps) {
  return (
    <Button variant="ghost" size="icon" onClick={onToggle}>
      {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
    </Button>
  )
}
