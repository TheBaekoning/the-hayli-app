<script lang="ts">
	import { session, logout } from '$components/session/session.store';
	import { preloadData } from '$app/navigation';
	import { page } from '$app/stores';
	import Emergency from '$components/988/988.svelte';

	let hamburgerButton = $state(null);
	let mobileMenu = $state(null);
	let open = $state(false);
	let ariaExpanded = $state('false');
	let menuLabel = $state('Open Menu');
	let logoLink = $state('/');
	let loggedIn = $state(false);
	session.subscribe(session => {
		if (typeof session.loggedIn === 'boolean') {
			logoLink = session.loggedIn ? '/health' : '/';
			loggedIn = session.loggedIn;
		}
		else logoLink = '/';
	});
	page.subscribe(() => {
		open = false;
		ariaExpanded = 'false';
		menuLabel = 'Open Menu';
	});
	function toggleMenu() {
		open = !open;
		ariaExpanded = open ? 'true' : 'false';
		menuLabel = open ? 'Close Menu' : 'Open Menu';
	}
	function preloadUnauth(){
		preloadData('/');
	}

	const links = [
		{href: '/health', text: 'Dashboard'},
		{href: '/health/mood', text: 'Moods'}
	];

</script>
<svelte:options runes={true} />

<nav id="main-nav">
	{#if loggedIn}
		<div>
			<button
				id="hamburger-button"
				bind:this={hamburgerButton}
				onclick={toggleMenu}
				class="not-button"
				aria-expanded={open}
			>
				<img
					id="hamburger"
					alt={menuLabel}
					src="/icons/hamburger-menu.svg"
					width="34"
					height="31"
				>
			</button>
		</div>
		<div id="mobile-menu-main-nav" bind:this={mobileMenu} class:open>
			<ul class="hide-list-markers">
				{#each links as {href, text}}
					<li><a {href}>{text}</a></li>
				{/each}
			</ul>
			<button class="not-button" onclick={logout} onmouseover={preloadUnauth} onfocus={preloadUnauth}>Log out</button>
		</div>
		<div id="desktop-menu-main-nav">
			<ul class="hide-list-markers">
				{#each links as {href, text}}
					<li><a {href}>{text}</a></li>
				{/each}
			</ul>
			<div class="right">
				<a href="/account">Account</a>
				<button class="not-button" onclick={logout} onmouseover={preloadUnauth} onfocus={preloadUnauth}>Log out</button>
			</div>
		</div>
	{:else}
		<div class="hayli-logo"><a href="/">Hayli</a></div>
	{/if}
	<Emergency />
</nav>

<style lang="scss">
	#main-nav {
		display: flex;
		position: relative;
		justify-content: space-between;
		align-items: center;
		gap: 40px;
		position: relative;
		padding: 1rem var(--side-spacing);
		z-index: 1;
	}
	.hayli-logo {
		font: 700 1.2rem/1 var(--ff);
		a {
			color: var(--dark2);
			text-decoration: none;
			cursor: pointer;
		}
	}
	#hamburger-button{
		display: block;
	}
	#hamburger {
		display: block;
	}
	#desktop-menu-main-nav {
		display: none;
		@media (min-width: 1000px){
			flex: 1 1 auto;
			display: flex;
			width: 100%;
			ul {
				display: flex;
				gap: 20px;
			}
			.right{
				flex: 1 0 auto;
				display: flex;
				justify-content: flex-end;
				gap: 10px;
				> a, > button{
					color: var(--green1);
				}
			}
		}
	}
	#mobile-menu-main-nav {
		position: absolute;
		top: 100%;
		max-height: 0;
		margin: 1px calc(-1 * var(--side-spacing)) 0;
		background: #fff;
		border-bottom: 1px solid transparent;
		transition: max-height 0.5s;
		overflow: hidden;
		width: 100%;
		z-index: 50;
		&.with-padding {
			padding: var(--side-spacing);
			max-width: calc(100% - 2 * var(--side-spacing));
			border-bottom-color: var(--dark1);
		}
		&.open {
			max-height: calc(100vh - (2 * 1rem + 40px + 2 * var(--side-spacing)));
			overflow: scroll;
		}
		a,
		button {
			padding: 0 20px;
			font: 700 1rem var(--ff);
			color: var(--dark1);
			text-decoration: underline;
			text-decoration-thickness: 2px;
		}
		--space: 10px;
		> ul.hide-list-markers {
			display: flex;
			flex-flow: column;
			max-width: 100%;
			padding: var(--space) 0;
			border-bottom: 1px solid var(--dark1);
			li + li {
				padding-top: var(--space);
				margin-top: var(--space);
				border-top: 1px solid var(--dark1);
			}
		}
		>button{
			padding-top: var(--space);
			padding-bottom: var(--space);
		}

	}
	@media (prefers-color-scheme: dark) {
		#main-nav {
			border-bottom-color: var(--light2);
			.hayli-logo a {
				color: var(--light2);
			}
		}
		#hamburger-button {
			background: var(--dark2);
		}
		#hamburger .line {
			stroke: var(--light2);
		}
		#mobile-menu-main-nav {
			background: var(--dark2);
			border-bottom-color: var(--light2);
			a,
			button {
				color: var(--light2);
			}
			> ul.hide-list-markers {
				border-bottom-color: var(--light2);
				li + li {
					border-top-color: var(--light2);
				}
			}
		}
	}
</style>
