export const config = {
    telegram: {
        botUsername: import.meta.env.VITE_TELEGRAM_BOT_USERNAME || 'RedditTop20Bot',
        get botUrl() {
            return `https://t.me/${this.botUsername}`;
        }
    }
} as const;