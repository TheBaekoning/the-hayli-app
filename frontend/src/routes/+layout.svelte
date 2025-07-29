<svelte:options runes={true} />
<script lang="ts">
	import MainNav from '$components/main-nav/main-nav.svelte';
	import OnStart from '$components/on-start/on-start.svelte';
	import Session from '$components/session/session.svelte';
	import type { Snippet } from 'svelte';
	import posthog from 'posthog-js'
	import { browser } from '$app/environment';
	import './styles.scss';
	import { afterNavigate } from '$app/navigation';

	let { children }: { children: Snippet } = $props();

	$effect(() => {
		if (browser){
			afterNavigate(() => {
				posthog.capture('$pageview');
			});
		}
	});

	$effect(() => {
		if (browser) {
			posthog.init(
				'phc_kZ45pSHsg0TVXGjtEu87juQxMpVc8NPEw4A0gZGhzBQ',
				{
					api_host: 'https://us.i.posthog.com',
					person_profiles: 'identified_only', // or 'always' to create profiles for anonymous users as well
					capture_pageview: false,
					disable_session_recording: true,
				}
			);
		}
	});
</script>

<svelte:head>
	<link rel="preload" href="/fonts/Reem-Kufi/reem-kufi-latin.woff2" as="font" type="font/woff2" crossorigin="anonymous">
	<link rel="stylesheet" href="/fonts/Reem-Kufi/fonts.css">
</svelte:head>

<Session />
<OnStart />
<MainNav />
{@render children()}