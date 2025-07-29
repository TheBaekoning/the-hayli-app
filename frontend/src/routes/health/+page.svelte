<script lang="ts">
	import { session } from '$components/session/session.store';
	import CtaCard from '$components/cta-card/cta-card.svelte';
	import InspirationalQuote from '$components/inspirational-quote/inspirational-quote.svelte';
	import { fromStore } from 'svelte/store';
	import type MoodModel from '$lib/models/mood';

	let { data }: { data: {moods: MoodModel[]}} = $props();

	const sess = fromStore(session);

	let firstName = $derived(sess.current.userProfile !== 'loading' ? sess.current.userProfile.firstName : '');

	$effect(() => {
		if (!data.moods.length) return;
		const obs = new IntersectionObserver(entries => {
			for(let entry of entries){
				entry.target.classList.add('intersected');
				obs.unobserve(entry.target);
				if (entries.length === 1) obs.disconnect();
			}
		}, {
			threshold: 0.5
		});
		function observeCards(){
			const cards = document.querySelectorAll('.action-cards .cta-card');
			if (cards.length){
				cards.forEach(card => {
					obs.observe(card);
				});
				return;
			}
			setTimeout(observeCards, 500);
		}
		observeCards();
		return () => obs.disconnect();
	});

	const entryToday = $derived.by(() => {
		if (!data || !Array.isArray(data.moods) || !data.moods.length) return false;
		const entry = data.moods[0];
		if (!entry) return false;
		
		const today = new Date();
		const date = new Date(entry.date);
	
		// Adjust for timezone offset timey wimey stuff
		const offsetHours = Math.round(date.getTimezoneOffset() / 60);
		date.setHours(date.getHours() + offsetHours);
		const offsetMinutes = Math.round(date.getTimezoneOffset() % 60);
		date.setMinutes(date.getMinutes() + offsetMinutes);
	
		return date.toDateString() === today.toDateString();
	});
	const days = $derived.by((): number => {
		if (!data) return 0;
		const { moods } = data;
		if (!moods?.length) return;
		const today = new Date();
		const lastEntry = new Date(moods[0].date);
		const diff = Math.abs(today.getTime() - lastEntry.getTime());
		const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
		return days;
	});
	const entriesThisPastMonth = $derived.by((): number => {
		const { moods } = data;
		if (!moods?.length) return;
		const today = new Date();
		const todayOffset = new Date(today.getTime() + (today.getTimezoneOffset() * 60 * 1000));
		const entries = moods.filter(mood => {
			const date = new Date(mood.date);
			const localTZOffset = new Date(date.getTime() + (date.getTimezoneOffset() * 60 * 1000));
			return localTZOffset.getTime() > todayOffset.getTime() - (31 * 24 * 60 * 60 * 1000);
		});
		return entries.length;
	});
</script>
<svelte:options runes={true} />
<svelte:head>
	<title>Dashboard</title>
</svelte:head>
<main class="health-index">
	{#if sess.current.userProfile === 'loading' || !!sess.current.userProfile.firstName}
		<h1>Hello {firstName}!</h1>
	{:else}
		<h1>Hello <a href="/account">there</a>!</h1>
	{/if}
	<div class="action-cards">
		{#if data?.moods?.length}
			{#if !entryToday}
				<CtaCard href="/health/mood/create" mainCTA={true}>
					<div class="days">{days}</div>
					<div>
						<div class="cta-title small">day{days > 1 ? 's' : ''} since your last entry.</div>
						<p class="sr-visible">It looks like you haven't added a mood entry for today. <span class="link">Add one now!</span></p>
					</div>
				</CtaCard>
			{:else if entriesThisPastMonth}
				<CtaCard href="/health/mood">
					<div class="days">{entriesThisPastMonth}</div>
					<div>
						<div class="cta-title small">entr{entriesThisPastMonth > 1 ? 'ies' : 'y'} this past month!</div>
					</div>
				</CtaCard>
			{/if}
		{:else}
			<CtaCard href="/health/mood/create" mainCTA={true}>
				<div></div>
				<div>
					<div class="cta-title big">Create your first entry!</div>
					<p class="sr-visible">It looks like you haven't added any mood entries yet. <span class="link">Add your first one now!</span></p>
				</div>
			</CtaCard>
		{/if}
		<CtaCard href="/health">
			<img loading="lazy" src="/images/logo/hayli-logo.svg" alt="" width="360" height="300">
			<div>
				<div class="cta-title">Chat with Hayli</div>
			</div>
		</CtaCard>
		{#if data?.moods?.length}
			<CtaCard href="/health/mood">
				<img loading="lazy" src="/icons/calendar.svg" alt="" width="95" height="85">
				<div class="cta-title small">Track your entries over time.</div>
			</CtaCard>
		{/if}
		<InspirationalQuote />
	</div>
</main>

<style lang="scss">
	main.health-index{
		h1{
			font: 700 1.625rem var(--ff);
			margin-bottom: 0;
		}
		.action-cards{
			display: flex;
			flex-flow: column;
			gap: 30px;
			margin-top: 40px;
			container: action-cards / size;
			:global(.cta-card){
				filter: opacity(0);
				transform: translateY(60px) rotate(-5deg);
				transition: all 0.5s ease;
			}
			:global(.cta-card.intersected){
				filter: opacity(1);
				transform: translateY(0);
				&:hover, &:focus{
					transform: translateY(-5px);
				}
			}
		}
	}
	@media (min-width: 1000px){
		main.health-index{
			.action-cards{
				display: grid;
				grid-template-columns: repeat(3, 1fr);
				gap: 30px;
				> :global(:first-child){
					grid-column: 1 / 4;
				}
			}
		}
	}
</style>