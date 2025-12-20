import { useThemes } from "@/hooks/use-theme";
import { cn } from "@/lib/utils";
import { Icons } from "./icons";

export function ThemeToggle() {
  const { theme, setTheme } = useThemes();

  const handleDarkMode = () => {
    setTheme(theme === "dark" ? "light" : "dark");
  };

  return (
    <button
      onClick={handleDarkMode}
      className="flex border border-primary transition-all dark:border-none dark:bg-muted  p-1 items-center rounded-none justify-center"
      type="button"
    >
      <div
        className={cn(
          "p-1.5 rounded-none text-primary hidden",
          theme === "light" && "text-background bg-primary block"
        )}
      >
        <Icons.sun size={17} />
      </div>
      <div
        className={cn(
          "p-1.5 rounded-none text-primary hidden",
          theme === "dark" && "text-background bg-primary block"
        )}
      >
        <Icons.moon size={17} />
      </div>
    </button>
  );
}
