<script lang="ts">
	/**
	 * @see https://www.embracehealth.com/blog/50-quotes-about-mental-health
	 */
	const quotes = [
		'Your mental health is just as important as your physical health.',
		'It’s okay not to be okay.',
		'You are not your mental illness.',
		'Your struggles do not define you.',
		'Taking care of your mental health is an act of self-love.',
		'You are worthy of happiness and peace of mind.',
		'There is no shame in seeking help for your mental health.',
		'It’s okay to take a break and prioritize your mental health.',
		'You are not alone in your struggles.',
		'It’s okay to ask for support when you need it.',
		"Mental health is not a destination, it’s a journey.",
		"Your mental health matters more than any external validation.",
		"Healing is not linear, but it is possible.",
		"You are stronger than you realize.",
		"Your mental health journey is unique to you.",
		"Self-care is not selfish, it’s necessary for good mental health.",
		"Small steps can lead to big progress in mental health.",
		"You are capable of overcoming your mental health challenges.",
		"Mental illness is not a personal failure, it’s a medical condition.",
		"Your mental health should not be stigmatized.",
		"You are deserving of a life free from mental health struggles.",
		"Your mental health journey may be difficult, but it’s worth it.",
		"You are not weak for struggling with mental health issues.",
		"It’s okay to take medication for your mental health.",
		"You are not a burden for seeking help for your mental health.",
		"Mental health issues do not make you any less of a person.",
		"Your mental health is just as important as your career or education.",
		"You are capable of managing your mental health and living a fulfilling life.",
		"You have the power to overcome your mental health challenges.",
		"It’s okay to take time to focus on your mental health.",
		 "You are deserving of love and compassion, especially from yourself.",
		 "Your mental health struggles do not define your future.",
		 "It’s okay to prioritize your mental health over other commitments.",
		 "You are not alone in your journey towards better mental health.",
		 "You are not your thoughts or emotions.",
		 "You are capable of creating a positive mindset and outlook on life.",
		 "Mental health recovery is possible, and it starts with seeking help.",
		 "You are worthy of a life filled with joy and happiness.",
		 "It’s okay to have bad days and ask for support when you need it.",
		"You have the power to change your relationship with your mental health.",
		"Your mental health journey may be a lifelong process, but it’s worth it.",
		"You are capable of breaking free from negative thought patterns.",
		"Mental health is just as important as physical health when it comes to overall wellness.",
		"You have the power to choose how you respond to your mental health challenges.",
		"It’s okay to take time to rest and recharge when you’re feeling overwhelmed.",
		"You are not alone in your struggles, even if it feels that way.",
		"Mental health is a journey of self-discovery and growth.",
		"You are worthy of receiving support and love, even on your toughest days.",
		"It’s okay to set boundaries to protect your mental health.",
		"You are capable of creating a life filled with purpose and meaning, despite your mental health challenges."
	];
</script>
<svelte:head>
	<link rel="preload" href="/fonts/Sura/Sura-Regular-subset.woff2" as="font" type="font/woff2" crossorigin="anonymous">
	<link rel="stylesheet" href="/fonts/Sura/fonts.css">
</svelte:head>
<blockquote>
	<p>{quotes[Math.floor(Math.random() * quotes.length)]}</p>
</blockquote>

<style lang="scss">
	blockquote {
		display: flex;
		align-items: center;
		gap: 20px;
		margin: 0;
		padding: 16px;
		border: 1px solid var(--spot4);
		background: #fff;
		border-radius: 15px;
		font: 600 1rem/1.5 var(--ff);
		color: var(--spot4);
		&::before{
			content: '';
			display: block;
			width: 100px;
			height: 84.4px;
			background: url('/icons/quote.svg') center/contain no-repeat;
		}
		p:first-child{
			margin-top: 0;
		}
		p:last-child{
			margin-bottom: 0;
		}
	}
</style>